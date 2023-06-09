package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnLootLevel;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.math.Range;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class HunterEnchantment extends CustomEnchantment {
	public HunterEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.BOW_AND_CROSSBOW )
			.slots( EquipmentSlots.BOTH_HANDS )
			.maxLevel( 3 )
			.minLevelCost( level->level * 9 + 6 )
			.maxLevelCost( level->level * 9 + 26 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig penaltyMultiplier = new DoubleConfig( -0.10, new Range<>( -0.33, 0.0 ) );
		final DoubleConfig distanceMultiplier = new DoubleConfig( 0.01, new Range<>( 0.0, 1.0 ) );
		final Supplier< HunterEnchantment > enchantment = Registries.HUNTER;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Hunter" )
				.comment( "Increases mob drops and makes the damage to scale with a distance." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnLootLevel.listen( this::increaseLootingLevel )
				.addCondition( Condition.predicate( data->data.source != null && data.source.is( DamageTypeTags.IS_PROJECTILE ) ) )
				.addCondition( Condition.predicate( data->this.getEnchantmentLevel( data.source ) > 0 ) )
				.insertTo( group );

			OnPreDamaged.listen( this::modifyDamage )
				.addCondition( Condition.predicate( data->data.attacker != null ) )
				.addCondition( Condition.predicate( data->data.source.is( DamageTypeTags.IS_PROJECTILE ) ) )
				.addCondition( Condition.predicate( data->this.getEnchantmentLevel( data.source ) > 0 ) )
				.addConfig( this.penaltyMultiplier.name( "penalty_multiplier" ).comment( "Damage multiplier penalty per enchantment level." ) )
				.addConfig( this.distanceMultiplier.name( "extra_multiplier" )
					.comment( "Extra damage multiplier bonus per each block to a target and per enchantment level." )
				).insertTo( group );
		}

		private void increaseLootingLevel( OnLootLevel.Data data ) {
			data.event.setLootingLevel( data.event.getLootingLevel() + this.getEnchantmentLevel( data.source ) );
		}

		private void modifyDamage( OnPreDamaged.Data data ) {
			assert data.attacker != null;
			float distance = Math.max( 0.0f, data.target.distanceTo( data.attacker ) - 1.0f );
			float level = this.getEnchantmentLevel( data.source );
			float damageMultiplier = level * ( this.penaltyMultiplier.asFloat() + distance * this.distanceMultiplier.asFloat() );

			data.extraDamage += data.damage * damageMultiplier;
			if( damageMultiplier > 0.0f ) {
				data.spawnMagicParticles = true;
			}
		}

		private int getEnchantmentLevel( DamageSource source ) {
			ItemStack weapon = IMixinProjectile.getWeaponFromDirectEntity( source );
			return weapon != null ? this.enchantment.get().getEnchantmentLevel( weapon ) : 0;
		}
	}
}
