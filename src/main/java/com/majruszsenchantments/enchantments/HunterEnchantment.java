package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnLootLevel;
import com.mlib.math.Range;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public class HunterEnchantment extends CustomEnchantment {
	public HunterEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.BOW_AND_CROSSBOW )
			.slots( EquipmentSlots.BOTH_HANDS )
			.maxLevel( 3 )
			.minLevelCost( level->level * 9 + 6 )
			.maxLevelCost( level->level * 9 + 26 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	private static class Modifier extends EnchantmentModifier< HunterEnchantment > {
		final DoubleConfig penaltyMultiplier = new DoubleConfig( -0.10, new Range<>( -0.33, 0.0 ) );
		final DoubleConfig distanceMultiplier = new DoubleConfig( 0.01, new Range<>( 0.0, 1.0 ) );

		public Modifier() {
			super( Registries.HUNTER, Registries.Modifiers.ENCHANTMENT );

			new OnLootLevel.Context( this::increaseLootingLevel )
				.addCondition( data->data.source != null && data.source.isProjectile() )
				.addCondition( data->this.getEnchantmentLevel( data.source ) > 0 )
				.insertTo( this );

			new OnDamaged.Context( this::modifyDamage )
				.addCondition( data->data.attacker != null )
				.addCondition( data->data.source.isProjectile() )
				.addCondition( data->this.getEnchantmentLevel( data.source ) > 0 )
				.addConfig( this.penaltyMultiplier.name( "penalty_multiplier" ).comment( "Damage multiplier penalty per enchantment level." ) )
				.addConfig( this.distanceMultiplier.name( "extra_multiplier" )
					.comment( "Extra damage multiplier bonus per each block to a target and per enchantment level." )
				).insertTo( this );

			this.name( "Hunter" ).comment( "Increases mob drops and makes the damage to scale with a distance." );
		}

		private void increaseLootingLevel( OnLootLevel.Data data ) {
			data.event.setLootingLevel( data.event.getLootingLevel() + this.getEnchantmentLevel( data.source ) );
		}

		private void modifyDamage( OnDamaged.Data data ) {
			assert data.attacker != null;
			float distance = Math.max( 0.0f, data.target.distanceTo( data.attacker ) - 1.0f );
			float level = this.getEnchantmentLevel( data.source );
			float damageMultiplier = 1.0f + level * ( this.penaltyMultiplier.asFloat() + distance * this.distanceMultiplier.asFloat() );

			data.event.setAmount( data.event.getAmount() * damageMultiplier );
		}

		private int getEnchantmentLevel( DamageSource source ) {
			ItemStack weapon = IMixinProjectile.getWeaponFromDirectEntity( source );
			return weapon != null ? this.enchantment.get().getEnchantmentLevel( weapon ) : 0;
		}
	}
}
