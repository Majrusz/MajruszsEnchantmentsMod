package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleRangeConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.math.Range;

import java.util.function.Supplier;

public class DeathWishEnchantment extends CustomEnchantment {
	public DeathWishEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.MELEE )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->12 )
			.maxLevelCost( level->50 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleRangeConfig damageMultiplier = new DoubleRangeConfig( new Range<>( 1.0, 2.0 ), new Range<>( 1.0, 10.0 ) );
		final DoubleRangeConfig vulnerabilityMultiplier = new DoubleRangeConfig( new Range<>( 0.7, 1.2 ), new Range<>( 0.0, 10.0 ) );
		final Supplier< DeathWishEnchantment > enchantment = Registries.DEATH_WISH;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "DeathWish" )
				.comment( "Increases damage dealt equal to the percentage of health lost." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnPreDamaged.listen( this::increaseDamageDealt )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.attacker ) )
				.addConfig( this.damageMultiplier.name( "DamageMultiplier" )
					.comment( "Multiplies the damage dealt according to the missing health ratio.\nIn other words, the lower the health ratio, the more 'to' value is taken into account." )
				).insertTo( group );

			OnPreDamaged.listen( this::increaseDamageReceived )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.target ) )
				.addConfig( this.vulnerabilityMultiplier.name( "VulnerabilityMultiplier" )
					.comment( "Multiplies the damage taken according to the health ratio.\nIn other words, the higher the health ratio, the more 'to' value is taken into account." )
				).insertTo( group );
		}

		private void increaseDamageDealt( OnPreDamaged.Data data ) {
			float damageMultiplier = this.damageMultiplier.lerp( ( float )EntityHelper.getMissingHealthRatio( data.attacker ) ) - 1.0f;

			data.extraDamage += data.damage * damageMultiplier;
			if( damageMultiplier > 0.01f ) {
				data.spawnMagicParticles = true;
			}
		}

		private void increaseDamageReceived( OnPreDamaged.Data data ) {
			float damageMultiplier = this.vulnerabilityMultiplier.lerp( ( float )EntityHelper.getHealthRatio( data.target ) ) - 1.0f;

			data.extraDamage += data.damage * damageMultiplier;
		}
	}
}
