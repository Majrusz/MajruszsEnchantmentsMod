package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntitySignalCheck;
import com.mlib.gamemodifiers.contexts.OnEntitySignalReceived;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.contexts.OnPlayerTick;
import com.mlib.math.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;

public class SixthSenseEnchantment extends CustomEnchantment {
	public SixthSenseEnchantment() {
		this.rarity( Rarity.VERY_RARE )
			.category( EnchantmentCategory.ARMOR_HEAD )
			.slots( EquipmentSlots.HEAD )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
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
	public static class Modifier extends EnchantmentModifier< SixthSenseEnchantment > {
		static final ResourceLocation LOOT_ID = Registries.getLocation( "chests/ancient_city_sixth_sense" );
		final DoubleConfig glowDuration = new DoubleConfig( 2.0, new Range<>( 0.5, 15.0 ) );

		public Modifier() {
			super( Registries.SIXTH_SENSE, Registries.Modifiers.ENCHANTMENT );

			new OnEntitySignalCheck.Context( OnEntitySignalCheck.DISPATCH )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.IsShiftKeyDown<>() )
				.addCondition( new Condition.IsOnGround<>() )
				.insertTo( this );

			new OnEntitySignalReceived.Context( this::highlightEntity )
				.addCondition( data->data.player instanceof ServerPlayer )
				.addCondition( data->data.owner != null && data.owner != data.player )
				.addConfig( this.glowDuration.name( "glow_duration" )
					.comment( "Determines how long the mob should be highlighted (this value stacks up with every sound emitted)." ) )
				.insertTo( this );

			new OnPlayerTick.Context( this::playSound )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.Cooldown< OnPlayerTick.Data >( Utility.secondsToTicks( 1.25 ), Dist.DEDICATED_SERVER ).setConfigurable( false ) )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.IsShiftKeyDown<>() )
				.addCondition( new Condition.IsOnGround<>() )
				.insertTo( this );

			new OnLoot.Context( this::addToChest )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new IsAncientCityChest() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.insertTo( this );

			this.name( "SixthSense" ).comment( "Highlights nearby mobs that emit any sound if the player is sneaking nearby." );
		}

		private void highlightEntity( OnEntitySignalReceived.Data data ) {
			EntityHelper.sendExtraClientGlowTicks( ( ServerPlayer )data.player, data.owner, this.glowDuration.asTicks() );
		}

		private void playSound( OnPlayerTick.Data data ) {
			SoundHandler.HEARTBEAT.play( data.level, data.player.position(), SoundHandler.randomized( 0.25f ) );
		}

		private void addToChest( OnLoot.Data data ) {
			data.addAsChestLoot( LOOT_ID );
		}
	}

	private static class IsAncientCityChest extends OnLoot.Is {
		public IsAncientCityChest() {
			super( BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY_ICE_BOX );

			this.ids.comment( "Determines which chests should contain this enchantment." );
		}
	}
}
