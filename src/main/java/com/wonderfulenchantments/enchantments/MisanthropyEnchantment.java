package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class MisanthropyEnchantment extends CustomEnchantment {
	public static Supplier< MisanthropyEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.MELEE_MINECRAFT, EquipmentSlots.MAINHAND, false, 5, level->-3 + 8 * level, level->17 + 8 * level );
		MisanthropyEnchantment enchantment = new MisanthropyEnchantment( params );
		Modifier modifier = new MisanthropyEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public MisanthropyEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< MisanthropyEnchantment > {
		final DoubleConfig damageBonus = new DoubleConfig( "damage_bonus", "Extra damage dealt to humans per enchantment level.", false, 2.5, 1.0, 10.0 );

		public Modifier( MisanthropyEnchantment enchantment ) {
			super( enchantment, "Misanthropy", "Increases the damage against villagers, pillagers, witches and other players." );

			OnDamagedContext onDamaged = new OnDamagedContext( this::modifyDamage );
			onDamaged.addCondition( data->data.level != null )
				.addCondition( data->data.attacker != null && enchantment.hasEnchantment( data.attacker ) )
				.addCondition( data->EntityHelper.isHuman( data.target ) );

			this.addConfig( this.damageBonus );
			this.addContext( onDamaged );
		}

		private void modifyDamage( OnDamagedData data ) {
			assert data.attacker != null && data.level != null;
			float extraDamage = this.enchantment.getEnchantmentLevel( data.attacker ) * this.damageBonus.asFloat();
			Vec3 position = data.target.position();

			data.level.sendParticles( ParticleTypes.ENCHANTED_HIT, position.x, data.target.getY( 0.625 ), position.z, 24, 0.125, 0.25, 0.125, 0.5 );
			data.event.setAmount( data.event.getAmount() + extraDamage );
		}
	}
}
