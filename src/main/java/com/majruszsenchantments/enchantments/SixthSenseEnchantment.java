package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.StringListConfig;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.*;
import com.mlib.math.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;

import java.util.function.Supplier;

public class SixthSenseEnchantment extends CustomEnchantment {
	public SixthSenseEnchantment() {
		this.rarity( Rarity.VERY_RARE )
			.category( EnchantmentCategory.ARMOR_HEAD )
			.slots( EquipmentSlots.HEAD )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean canEnchant( ItemStack itemStack ) {
		return this.category.canEnchant( itemStack.getItem() );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack itemStack ) {
		return false;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@AutoInstance
	public static class Handler {
		static final ResourceLocation LOOT_ID = Registries.getLocation( "chests/ancient_city_sixth_sense" );
		final DoubleConfig glowDuration = new DoubleConfig( 2.0, new Range<>( 0.5, 15.0 ) );
		final Supplier< SixthSenseEnchantment > enchantment = Registries.SIXTH_SENSE;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "SixthSense" )
				.comment( "Highlights nearby mobs that emit any sound if the player is sneaking nearby." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEntitySignalCheck.listen( OnEntitySignalCheck.DISPATCH )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addCondition( Condition.isShiftKeyDown( data->data.player ) )
				.addCondition( Condition.isOnGround( data->data.player ) )
				.insertTo( group );

			OnEntitySignalReceived.listen( this::highlightEntity )
				.addCondition( Condition.predicate( data->data.player instanceof ServerPlayer ) )
				.addCondition( Condition.predicate( data->data.owner != null && data.owner != data.player ) )
				.addConfig( this.glowDuration.name( "glow_duration" )
					.comment( "Determines how long the mob should be highlighted (this value stacks up with every sound emitted)." ) )
				.insertTo( group );

			OnPlayerTick.listen( this::playSound )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.< OnPlayerTick.Data > cooldown( Utility.secondsToTicks( 1.25 ), Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addCondition( Condition.isShiftKeyDown( data->data.player ) )
				.addCondition( Condition.isOnGround( data->data.player ) )
				.insertTo( group );

			OnLoot.listen( this::addToChest )
				.addCondition( Condition.isServer() )
				.addCondition( Handler.isAncientCityChest() )
				.addCondition( OnLoot.hasOrigin() )
				.insertTo( group );
		}

		private void highlightEntity( OnEntitySignalReceived.Data data ) {
			EntityHelper.sendExtraClientGlowTicks( ( ServerPlayer )data.player, data.owner, this.glowDuration.asTicks() );
		}

		private void playSound( OnPlayerTick.Data data ) {
			SoundHandler.HEARTBEAT.play( data.getLevel(), data.player.position(), SoundHandler.randomized( 0.3f ) );
		}

		private void addToChest( OnLoot.Data data ) {
			data.addAsChestLoot( LOOT_ID );
		}

		private static Condition< OnLoot.Data > isAncientCityChest() {
			Condition< OnLoot.Data > condition = OnLoot.is( BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY_ICE_BOX );
			if( condition.getConfigs().get( 0 ) instanceof StringListConfig config ) {
				config.comment( "Determines which chests should contain this enchantment." );
			}

			return condition;
		}
	}
}
