package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleRangeConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.math.Range;

public class DeathWishEnchantment extends CustomEnchantment {
	public DeathWishEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.MELEE )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->12 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< DeathWishEnchantment > {
		final DoubleRangeConfig damageMultiplier = new DoubleRangeConfig( new Range<>( 1.0, 2.0 ), new Range<>( 1.0, 10.0 ) );
		final DoubleRangeConfig vulnerabilityMultiplier = new DoubleRangeConfig( new Range<>( 0.7, 1.2 ), new Range<>( 0.0, 10.0 ) );

		public Modifier() {
			super( Registries.DEATH_WISH, Registries.Modifiers.ENCHANTMENT );

			new OnDamaged.Context( this::increaseDamageDealt )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.attacker ) )
				.addConfig( this.damageMultiplier.name( "DamageMultiplier" )
					.comment( "Multiplies the damage dealt according to the missing health ratio.\nIn other words, the lower the health ratio, the more 'to' value is taken into account." )
				).insertTo( this );

			new OnDamaged.Context( this::increaseDamageReceived )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.target ) )
				.addConfig( this.vulnerabilityMultiplier.name( "VulnerabilityMultiplier" )
					.comment( "Multiplies the damage taken according to the health ratio.\nIn other words, the higher the health ratio, the more 'to' value is taken into account." )
				).insertTo( this );

			this.name( "DeathWish" ).comment( "Increases damage dealt equal to the percentage of health lost." );
		}

		private void increaseDamageDealt( OnDamaged.Data data ) {
			float damageMultiplier = this.damageMultiplier.lerp( ( float )EntityHelper.getMissingHealthRatio( data.attacker ) );

			data.event.setAmount( data.event.getAmount() * damageMultiplier );
		}

		private void increaseDamageReceived( OnDamaged.Data data ) {
			float damageMultiplier = this.vulnerabilityMultiplier.lerp( ( float )EntityHelper.getHealthRatio( data.target ) );

			data.event.setAmount( data.event.getAmount() * damageMultiplier );
		}
	}
}
