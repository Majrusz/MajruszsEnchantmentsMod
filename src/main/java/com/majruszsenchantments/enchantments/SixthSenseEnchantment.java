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
import com.mlib.gamemodifiers.contexts.OnPlayerTick;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< SixthSenseEnchantment > {
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

			this.name( "SixthSense" ).comment( "Adds acquired items directly to player's inventory." );
		}

		private void highlightEntity( OnEntitySignalReceived.Data data ) {
			EntityHelper.sendExtraClientGlowTicks( ( ServerPlayer )data.player, data.owner, this.glowDuration.asTicks() );
		}

		private void playSound( OnPlayerTick.Data data ) {
			SoundHandler.HEARTBEAT.play( data.level, data.player.position(), SoundHandler.randomized( 0.25f ) );
		}
	}
}
