package com.wonderfulenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class FanaticEnchantment extends Enchantment {
	public FanaticEnchantment( String name ) {
		super( Rarity.UNCOMMON, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 6;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 * ( level ) + ( ConfigHandler.Enchantments.FISHING_FANATIC ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	@Override
	public float calcDamageByCreature( int level, EnumCreatureAttribute creature ) {
		return ( float )level * 1.0F;
	}

	@Override
	protected boolean canApplyTogether( Enchantment enchant ) {
		return super.canApplyTogether( enchant );
	}

	@SubscribeEvent
	public String getTranslatedName( int level ) {
		if( level == this.getMaxLevel() )
			return TextFormatting.GRAY + new TextComponentTranslation( "wonderful_enchantments.true_level" ).getUnformattedText() + " " + new TextComponentTranslation( this.getName() ).getUnformattedText();

		return super.getTranslatedName( level );
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@SubscribeEvent
	public static void fishingFanaticEvent( ItemFishedEvent event ) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.getEntityWorld();

		LootContext.Builder lootContext$builder = ( new LootContext.Builder( ( WorldServer )world ) ).withPlayer( player ).withLuck( player.getLuck() );

		LootTable loottable = player.getEntityWorld().getLootTableManager().getLootTableFromLocation( LootTableList.GAMEPLAY_FISHING );

		EntityFishHook fishingBobber = event.getHookEntity();
		String reward = event.getDrops().get( 0 ).getDisplayName();

		int fanaticLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC, player );

		Multiset< String > rewards = HashMultiset.create();
		rewards.add( reward );
		int extraItemsCounter = 0;
		for( int i = 0; i < fanaticLevel && ConfigHandler.Enchantments.FISHING_FANATIC; i++ ) {
			if( WonderfulEnchantments.RANDOM.nextFloat() < 0.33334f )
				for( ItemStack itemstack : loottable.generateLootForPools( WonderfulEnchantments.RANDOM, lootContext$builder.build() ) ) {
					EntityItem entityItem = new EntityItem( world, fishingBobber.posX + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(), fishingBobber.posY + 0.25D * WonderfulEnchantments.RANDOM.nextDouble(), fishingBobber.posZ + 0.50D * WonderfulEnchantments.RANDOM.nextDouble(), itemstack );

					double deltaX = player.posX - entityItem.posX, deltaY = player.posY - entityItem.posY, deltaZ = player.posZ - entityItem.posZ;

					entityItem.setVelocity( 0.1D * deltaX, 0.1D * deltaY + Math.pow( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) + Math.pow( deltaZ, 2 ), 0.25D ) * 0.08D, 0.1D * deltaZ );
					world.spawnEntity( entityItem );

					rewards.add( itemstack.getDisplayName() );
					extraItemsCounter++;
				}
		}

		if( tryIncreaseFishingFanaticLevel( player ) )
			player.sendStatusMessage( new TextComponentString( TextFormatting.BOLD + new TextComponentTranslation( "wonderful_enchantments.fanatic_level_up" ).getUnformattedText() ), true );

		else if( rewards.size() > 1 )
			notifyPlayerAboutRewards( reward, rewards, player );

		event.damageRodBy( event.getRodDamage() + extraItemsCounter );
		world.spawnEntity( new EntityXPOrb( world, player.posX, player.posY + 0.5D, player.posZ, extraItemsCounter + WonderfulEnchantments.RANDOM.nextInt( 2 * extraItemsCounter + 1 ) ) );
	}

	private static boolean tryIncreaseFishingFanaticLevel( EntityPlayer player ) {
		ItemStack fishingRod = player.getHeldItemMainhand();
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FISHING_FANATIC, player );
		double increaseChance = ( RegistryHandler.FISHING_FANATIC.getMaxLevel() - enchantmentLevel ) / 100.0D;

		boolean shouldIncreaseLevel = ( WonderfulEnchantments.RANDOM.nextDouble() < increaseChance );

		if( shouldIncreaseLevel && ( enchantmentLevel < RegistryHandler.FISHING_FANATIC.getMaxLevel() ) ) {
			if( enchantmentLevel == 0 )
				fishingRod.addEnchantment( RegistryHandler.FISHING_FANATIC, 1 );
			else {
				NBTTagList nbt = fishingRod.getEnchantmentTagList();

				for( int i = 0; i < nbt.tagCount(); ++i )
					if( nbt.getCompoundTagAt( i ).getString( "id" ).contains( "fishing_fanatic" ) ) {
						nbt.getCompoundTagAt( i ).setInteger( "lvl", enchantmentLevel + 1 );
						break;
					}

				fishingRod.setTagInfo( "Enchantments", nbt );
			}

			return true;
		} else
			return false;
	}

	private static void notifyPlayerAboutRewards( String reward, Multiset< String > rewards, EntityPlayer player ) {
		TextComponentString message = new TextComponentString( TextFormatting.WHITE + "(" );

		ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards ).elementSet().asList();
		for( int i = 0; i < rewardList.size(); i++ ) {
			message.appendSibling( new TextComponentString( ( ( i == 0 ) ? TextFormatting.WHITE : TextFormatting.GOLD ) + rewardList.get( i ) ) );

			if( rewards.count( rewardList.get( i ) ) > 1 )
				message.appendSibling( new TextComponentString( TextFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );

			if( i != rewardList.size() - 1 )
				message.appendSibling( new TextComponentString( TextFormatting.WHITE + ", " ) );
		}

		message.appendSibling( new TextComponentString( TextFormatting.WHITE + ")" ) );
		player.sendStatusMessage( message, true );
	}
}
