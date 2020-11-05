package com.wonderfulenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber
public class FanaticEnchantment extends Enchantment {
    public FanaticEnchantment() {
        super( Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND } );
    }

    @Override
    public int getMinEnchantability( int level ) {
        return 10 * ( level );
    }

    @Override
    public int getMaxEnchantability( int level ) {
        return this.getMinEnchantability( level ) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 6;
    }

    @Override
    protected boolean canApplyTogether( Enchantment enchant ) {
        return super.canApplyTogether( enchant );
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public float calcDamageByCreature( int level, CreatureAttribute creature ) {
        return ( float )level * 1.0F;
    }

    @Override
    public ITextComponent getDisplayName( int level ) {
        if( level == this.getMaxLevel() ) {
            StringTextComponent message = new StringTextComponent( TextFormatting.GRAY + new TranslationTextComponent( "wonderful_enchantments.true_level" ).getUnformattedComponentText() );
            message.appendSibling( new StringTextComponent( TextFormatting.GRAY + " " + new TranslationTextComponent( this.getName() ).getUnformattedComponentText() ) );

            return message;

        } else
            return super.getDisplayName( level );
    }

    @SubscribeEvent
    public static void fishingFanaticEvent( ItemFishedEvent event ) {
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();

        LootContext.Builder lootContext$builder = ( new LootContext.Builder( (ServerWorld)world ) )
                .withParameter( LootParameters.POSITION, player.getPosition() )
                .withParameter( LootParameters.TOOL, player.getHeldItemMainhand() )
                .withRandom( WonderfulEnchantments.RANDOM )
                .withLuck( player.getLuck() );

        LootTable loottable = ServerLifecycleHooks.getCurrentServer().getLootTableManager().getLootTableFromLocation( LootTables.GAMEPLAY_FISHING );

        FishingBobberEntity fishingBobber = event.getHookEntity();
        String reward = event.getDrops().get( 0 ).getDisplayName().getString();

        int fanaticLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC.get(), player );

        Multiset< String > rewards = HashMultiset.create();
        rewards.add( reward );
        int extraItemsCounter = 0;
        for( int i = 0; i < fanaticLevel; i++ ) {
            if( WonderfulEnchantments.RANDOM.nextFloat() < 0.33334f )
                for( ItemStack itemstack : loottable.generate( lootContext$builder.build( LootParameterSets.FISHING ) ) ) {
                    ItemEntity entityItem = new ItemEntity(
                        world,
                        fishingBobber.getPosX() + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(),
                        fishingBobber.getPosY() + 0.25D * WonderfulEnchantments.RANDOM.nextDouble(),
                        fishingBobber.getPosZ() + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(),
                        itemstack
                    );

                    double	deltaX = player.getPosX()-entityItem.getPosX(),
                            deltaY = player.getPosY()-entityItem.getPosY(),
                            deltaZ = player.getPosZ()-entityItem.getPosZ();

                    entityItem.setMotion(
                        0.1D * deltaX,
                        0.1D * deltaY + Math.pow( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) + Math.pow( deltaZ, 2 ), 0.25D ) * 0.08D,
                        0.1D * deltaZ
                    );
                    world.addEntity( entityItem );

                    rewards.add( entityItem.getDisplayName().getString() );
                    extraItemsCounter++;
                }
        }

        if( tryIncreaseFishingFanaticLevel( player ) )
            player.sendStatusMessage( new StringTextComponent(
            TextFormatting.BOLD + new TranslationTextComponent( "wonderful_enchantments.fanatic_level_up" ).getUnformattedComponentText()
            ), true );

        else if( rewards.size() > 1 )
            notifyPlayerAboutRewards( reward, rewards, player );

        event.damageRodBy( event.getRodDamage() + extraItemsCounter );
        world.addEntity( new ExperienceOrbEntity(
            world,
            player.getPosX(),
            player.getPosY() + 0.5D,
            player.getPosZ() + 0.5D,
            extraItemsCounter + WonderfulEnchantments.RANDOM.nextInt( 2*extraItemsCounter+1 )
        ) );
    }

    private static boolean tryIncreaseFishingFanaticLevel( PlayerEntity player ) {
        ItemStack fishingRod = player.getHeldItemMainhand();
        int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC.get(), player );
        double increaseChance = ( RegistryHandler.FISHING_FANATIC.get().getMaxLevel() - enchantmentLevel )/100.0D;

        boolean shouldIncreaseLevel = ( WonderfulEnchantments.RANDOM.nextDouble() < increaseChance );

        if( shouldIncreaseLevel && ( enchantmentLevel < RegistryHandler.FISHING_FANATIC.get().getMaxLevel() ) ) {
            if( enchantmentLevel == 0 )
                fishingRod.addEnchantment( RegistryHandler.FISHING_FANATIC.get(), 1 );
            else {
                ListNBT nbt = fishingRod.getEnchantmentTagList();

                for( int i = 0; i < nbt.size(); ++i )
                    if( nbt.getCompound( i ).getString( "id" ).contains( "fishing_fanatic" ) ) {
                        nbt.getCompound( i ).putInt( "lvl", enchantmentLevel + 1 );
                        break;
                    }

                fishingRod.setTagInfo( "Enchantments", nbt );
            }

            return true;
        } else
            return false;
    }

    private static void notifyPlayerAboutRewards( String reward, Multiset< String > rewards, PlayerEntity player ) {
        StringTextComponent message = new StringTextComponent( TextFormatting.WHITE + "(" );

        ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards ).elementSet().asList();
        for( int i = 0; i < rewardList.size(); i++ ) {
            message.appendSibling( new StringTextComponent(
                ( ( i == 0 ) ? TextFormatting.WHITE : TextFormatting.GOLD ) + rewardList.get( i )
            ) );

            if( rewards.count( rewardList.get( i ) ) > 1 )
                message.appendSibling( new StringTextComponent( TextFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );

            if( i != rewardList.size()-1 )
                message.appendSibling( new StringTextComponent( TextFormatting.WHITE + ", " ) );
        }

        message.appendSibling( new StringTextComponent( TextFormatting.WHITE + ")" ) );
        player.sendStatusMessage( message, true );
    }
}
