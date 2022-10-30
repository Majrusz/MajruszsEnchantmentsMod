package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.math.VectorHelper;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
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

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DamageEnchantment ) && super.checkCompatibility( enchantment );
	}

	private static class Modifier extends EnchantmentModifier< MisanthropyEnchantment > {
		final DoubleConfig damageBonus = new DoubleConfig( "damage_bonus", "Extra damage dealt to humans per enchantment level.", false, 2.5, 1.0, 10.0 );

		public Modifier( MisanthropyEnchantment enchantment ) {
			super( enchantment, "Misanthropy", "Increases the damage against villagers, pillagers, witches and other players." );

			OnDamaged.Context onDamaged = new OnDamaged.Context( this::modifyDamage );
			onDamaged.addCondition( new Condition.IsServer() )
				.addCondition( data->data.attacker != null && enchantment.hasEnchantment( data.attacker ) )
				.addCondition( data->EntityHelper.isHuman( data.target ) )
				.addCondition( OnDamaged.DEALT_ANY_DAMAGE );

			this.addConfig( this.damageBonus );
			this.addContext( onDamaged );
		}

		private void modifyDamage( OnDamaged.Data data ) {
			assert data.attacker != null && data.level != null;
			float extraDamage = this.enchantment.getEnchantmentLevel( data.attacker ) * this.damageBonus.asFloat();

			data.event.setAmount( data.event.getAmount() + extraDamage );
			this.spawnParticles( data );
		}

		private void spawnParticles( OnDamaged.Data data ) {
			Vec3 position = VectorHelper.add( data.target.position(), new Vec3( 0.0, data.target.getBbHeight() * 0.625, 0.0 ) );
			Supplier< Vec3 > offset = ()->new Vec3( 0.125, 0.25, 0.125 );
			Supplier< Float > speed = ParticleHandler.speed( 0.5f );
			ParticleHandler.ENCHANTED_HIT.spawn( data.level, position, 24, offset, speed );
		}
	}
}
