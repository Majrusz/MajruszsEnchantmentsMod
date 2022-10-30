package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.gamemodifiers.contexts.OnLootLevel;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class HunterEnchantment extends CustomEnchantment {
	public static Supplier< HunterEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.BOW_AND_CROSSBOW, EquipmentSlots.BOTH_HANDS, false, 3, level->6 + 9 * level, level->26 + 9 * level );
		HunterEnchantment enchantment = new HunterEnchantment( params );
		Modifier modifier = new HunterEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public HunterEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< HunterEnchantment > {
		final DoubleConfig penaltyMultiplier = new DoubleConfig( "penalty_multiplier", "Damage multiplier penalty per enchantment level.", false, -0.10, -0.33, 0.0 );
		final DoubleConfig distanceMultiplier = new DoubleConfig( "extra_multiplier", "Extra damage multiplier bonus per each block to a target and per enchantment level.", false, 0.01, 0.0, 1.0 );

		public Modifier( HunterEnchantment enchantment ) {
			super( enchantment, "Hunter", "Increases mob drops and makes the damage to scale with a distance." );

			OnLootLevel.Context onLootLevel = new OnLootLevel.Context( this::increaseLootingLevel );
			onLootLevel.addCondition( data->data.source != null && data.source.isProjectile() )
				.addCondition( data->this.getEnchantmentLevel( data.source ) > 0 );

			OnDamaged.Context onDamaged = new OnDamaged.Context( this::modifyDamage );
			onDamaged.addCondition( data->data.attacker != null )
				.addCondition( data->data.source.isProjectile() )
				.addCondition( data->this.getEnchantmentLevel( data.source ) > 0 );

			this.addConfigs( this.penaltyMultiplier, this.distanceMultiplier );
			this.addContext( onLootLevel );
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
			return weapon != null ? this.enchantment.getEnchantmentLevel( weapon ) : 0;
		}
	}
}
