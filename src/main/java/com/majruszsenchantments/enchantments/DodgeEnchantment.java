package com.majruszsenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPreDamagedContext;
import com.mlib.gamemodifiers.data.OnPreDamagedData;
import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class DodgeEnchantment extends CustomEnchantment {
	public static Supplier< DodgeEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.ARMOR_LEGS, EquipmentSlots.LEGS, false, 2, level->level * 14, level->level * 14 + 20 );
		DodgeEnchantment enchantment = new DodgeEnchantment( params );
		Modifier modifier = new DodgeEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public DodgeEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< DodgeEnchantment > {
		final DoubleConfig chance = new DoubleConfig( "chance", "Chance to completely ignore the damage per enchantment level.", false, 0.125, 0.01, 0.4 );
		final DoubleConfig pantsDamageMultiplier = new DoubleConfig( "pants_damage_multiplier", "Percent of damage transferred to pants.", false, 0.5, 0.0, 10.0 );

		public Modifier( DodgeEnchantment enchantment ) {
			super( enchantment, "Dodge", "Gives a chance to completely avoid any kind of damage." );

			OnPreDamagedContext onDamaged = new OnPreDamagedContext( this::dodgeDamage );
			onDamaged.addCondition( new Condition.HasEnchantment( enchantment) )
				.addCondition( data->data.event.getAmount() > 0.0f )
				.addCondition( data->Random.tryChance( enchantment.getEnchantmentLevel( data.target ) * this.chance.asFloat() ) );

			this.addConfigs( this.chance, this.pantsDamageMultiplier );
			this.addContext( onDamaged );
		}

		private void dodgeDamage( OnPreDamagedData data ) {
			spawnEffects( data.target, data.level );
			damagePants( data.target, data.event.getAmount() );
			data.event.setCanceled( true );
		}

		private void spawnEffects( LivingEntity entity, ServerLevel level ) {
			if( level == null ) {
				return;
			}

			for( double d = 0.0; d < 3.0; d++ ) {
				Vec3 position = new Vec3( 0.0, entity.getBbHeight() * 0.25 * ( d + 1.0 ), 0.0 ).add( entity.position() );
				for( int i = 0; i < 2; i++ ) {
					level.sendParticles( Registries.DODGE_PARTICLE.get(), position.x, position.y, position.z, 5 * ( 2 * i + 1 ), ( i + 1 ) * 0.25, 0.375, ( i + 1 ) * 0.25, 0.0075 );
				}
			}
			level.playSound( null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.AMBIENT, 0.7f, 1.0f );
		}

		private void damagePants( LivingEntity entity, float damage ) {
			float multiplier = this.pantsDamageMultiplier.asFloat();
			if( multiplier > 0.0f ) {
				ItemStack pants = entity.getItemBySlot( EquipmentSlot.LEGS );
				int totalDamage = Math.max( 1, ( int )( damage * multiplier ) );
				pants.hurtAndBreak( totalDamage, entity, owner->owner.broadcastBreakEvent( EquipmentSlot.LEGS ) );
			}
		}
	}
}
