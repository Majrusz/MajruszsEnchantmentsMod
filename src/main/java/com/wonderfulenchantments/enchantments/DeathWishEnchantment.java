package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class DeathWishEnchantment extends CustomEnchantment {
	public static Supplier< DeathWishEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.MELEE, EquipmentSlots.MAINHAND, false, 1, level->12, level->50 );
		DeathWishEnchantment enchantment = new DeathWishEnchantment( params );
		Modifier modifier = new DeathWishEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public DeathWishEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< DeathWishEnchantment > {
		final DoubleConfig damageMultiplier = new DoubleConfig( "damage_multiplier", "Maximum damage multiplier obtainable at low health.", false, 2.0, 1.0, 10.0 );
		final DoubleConfig vulnerabilityMultiplier = new DoubleConfig( "vulnerability_multiplier", "Whenever the owner takes damage, the damage is multiplied by this value.", false, 1.25, 1.0, 10.0 );

		public Modifier( DeathWishEnchantment enchantment ) {
			super( enchantment, "DeathWish", "Increases damage dealt equal to the percentage of health lost." );

			OnDamagedContext onDamaged = new OnDamagedContext( this::increaseDamageDealt );
			onDamaged.addCondition( data->data.attacker != null );
			onDamaged.addCondition( data->enchantment.hasEnchantment( data.attacker ) );

			OnDamagedContext onDamaged2 = new OnDamagedContext( this::increaseDamageReceived );
			onDamaged2.addCondition( data->enchantment.hasEnchantment( data.target ) );

			this.addConfigs( this.damageMultiplier, this.vulnerabilityMultiplier );
			this.addContexts( onDamaged, onDamaged2 );
		}

		private void increaseDamageDealt( OnDamagedData data ) {
			data.event.setAmount( data.event.getAmount() * this.getDamageMultiplier( data.attacker ) );
		}

		private void increaseDamageReceived( OnDamagedData data ) {
			data.event.setAmount( data.event.getAmount() * this.vulnerabilityMultiplier.asFloat() );
		}

		private float getDamageMultiplier( LivingEntity entity ) {
			return ( float )EntityHelper.getMissingHealthRatio( entity ) * ( this.damageMultiplier.asFloat() - 1.0f ) + 1.0f;
		}
	}
}
