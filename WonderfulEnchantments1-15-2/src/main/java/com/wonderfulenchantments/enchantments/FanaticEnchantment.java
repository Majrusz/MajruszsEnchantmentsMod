package com.wonderfulenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
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
	protected static final double extraCatchChance = 0.33334D, levelIncreaseChanceMultiplier = 0.01D;

	public FanaticEnchantment() {
		super( Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 6;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 * ( level ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	@Override
	public float calcDamageByCreature( int level, CreatureAttribute creature ) {
		return ( float )level * 1.0F;
	}

	@Override
	public ITextComponent getDisplayName( int level ) {
		if( level == this.getMaxLevel() ) {
			String name;
			String enchantmentName = new TranslationTextComponent( this.getName() ).getUnformattedComponentText();
			String prefix = new TranslationTextComponent( "wonderful_enchantments.true_level" ).getUnformattedComponentText();

			name = String.format( "%s %s", prefix, enchantmentName );

			ITextComponent output = new StringTextComponent( name );
			output.applyTextStyle( TextFormatting.GRAY );

			return output;
		}

		return super.getDisplayName( level );
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@SubscribeEvent
	public static void onFishedItem( ItemFishedEvent event ) {
		PlayerEntity player = event.getPlayer();
		World world = player.getEntityWorld();

		LootContext lootContext = generateLootContext( player );
		LootTable lootTable = getFishingLootTable();
		int fanaticLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC.get(), player );

		Multiset< String > rewards = HashMultiset.create();
		rewards.add( event.getDrops().get( 0 ).getDisplayName().getString() );

		int extraRewardsCounter = 0;
		for( int i = 0; i < fanaticLevel && ConfigHandler.Values.FISHING_FANATIC.get(); i++ )
			if( WonderfulEnchantments.RANDOM.nextDouble() < extraCatchChance )
				for( ItemStack extraReward : lootTable.generate( lootContext ) ) {
					spawnReward( extraReward, player, world, event.getHookEntity() );

					rewards.add( extraReward.getDisplayName().getString() );
					extraRewardsCounter++;
				}

		if( tryIncreaseFishingFanaticLevel( player ) )
			player.sendStatusMessage( new StringTextComponent( TextFormatting.BOLD + new TranslationTextComponent( "wonderful_enchantments.fanatic_level_up" ).getUnformattedComponentText() ), true );

		else if( rewards.size() > 1 )
			notifyPlayerAboutRewards( rewards, player );

		event.damageRodBy( event.getRodDamage() + extraRewardsCounter );
		world.addEntity( new ExperienceOrbEntity( world, player.getPosX(), player.getPosY() + 0.5D, player.getPosZ() + 0.5D, extraRewardsCounter + WonderfulEnchantments.RANDOM.nextInt( 2 * extraRewardsCounter + 1 ) ) );
	}

	protected static LootContext generateLootContext( PlayerEntity player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerWorld )player.getEntityWorld() );
		lootContextBuilder.withParameter( LootParameters.POSITION, player.getPosition() )
			.withParameter( LootParameters.TOOL, player.getHeldItemMainhand() )
			.withRandom( WonderfulEnchantments.RANDOM )
			.withLuck( player.getLuck() );

		return lootContextBuilder.build( LootParameterSets.FISHING );
	}

	protected static LootTable getFishingLootTable() {
		return ServerLifecycleHooks.getCurrentServer().getLootTableManager().getLootTableFromLocation( LootTables.GAMEPLAY_FISHING );
	}

	protected static void spawnReward( ItemStack reward, PlayerEntity player, World world, FishingBobberEntity bobberEntity ) {
		ItemEntity itemEntity = new ItemEntity( world, bobberEntity.getPosX() + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(), bobberEntity.getPosY() + 0.25D * WonderfulEnchantments.RANDOM.nextDouble(), bobberEntity.getPosZ() + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(), reward );

		double deltaX = player.getPosX() - itemEntity.getPosX();
		double deltaY = player.getPosY() - itemEntity.getPosY();
		double deltaZ = player.getPosZ() - itemEntity.getPosZ();
		itemEntity.setMotion( 0.1D * deltaX, 0.1D * deltaY + Math.pow( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) + Math.pow( deltaZ, 2 ), 0.25D ) * 0.08D, 0.1D * deltaZ );

		world.addEntity( itemEntity );
	}


	protected static boolean tryIncreaseFishingFanaticLevel( PlayerEntity player ) {
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC.get(), player );
		double increaseChance = ( RegistryHandler.FISHING_FANATIC.get().getMaxLevel() - enchantmentLevel ) * levelIncreaseChanceMultiplier;

		if( WonderfulEnchantments.RANDOM.nextDouble() < increaseChance ) {
			ItemStack fishingRod = player.getHeldItemMainhand();

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
		}

		return false;
	}

	protected static void notifyPlayerAboutRewards( Multiset< String > rewards, PlayerEntity player ) {
		StringTextComponent message = new StringTextComponent( TextFormatting.WHITE + "(" );

		ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards ).elementSet().asList();
		for( int i = 0; i < rewardList.size(); i++ ) {
			message.appendSibling( new StringTextComponent( ( ( i == 0 ) ? TextFormatting.WHITE : TextFormatting.GOLD ) + rewardList.get( i ) ) );

			if( rewards.count( rewardList.get( i ) ) > 1 )
				message.appendSibling( new StringTextComponent( TextFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );

			if( i != rewardList.size() - 1 )
				message.appendSibling( new StringTextComponent( TextFormatting.WHITE + ", " ) );
		}

		message.appendSibling( new StringTextComponent( TextFormatting.WHITE + ")" ) );
		player.sendStatusMessage( message, true );
	}
}
