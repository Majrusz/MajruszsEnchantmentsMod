package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

public class DodgeEnchantment extends CustomEnchantment {
	public DodgeEnchantment() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR_LEGS )
			.slots( EquipmentSlots.LEGS )
			.maxLevel( 2 )
			.minLevelCost( level->level * 14 )
			.maxLevelCost( level->level * 14 + 20 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< DodgeEnchantment > {
		final DoubleConfig chance = new DoubleConfig( 0.125, new Range<>( 0.01, 0.4 ) );
		final DoubleConfig pantsDamageMultiplier = new DoubleConfig( 0.5, new Range<>( 0.0, 10.0 ) );

		public Modifier() {
			super( Registries.DODGE, Registries.Modifiers.ENCHANTMENT );

			new OnPreDamaged.Context( this::dodgeDamage )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( OnPreDamaged.DEALT_ANY_DAMAGE )
				.addCondition( OnPreDamaged.WILL_TAKE_FULL_DAMAGE )
				.addCondition( this::tryToDodge )
				.addConfig( this.chance.name( "chance" ).comment( "Chance to completely ignore the damage per enchantment level." ) )
				.addConfig( this.pantsDamageMultiplier.name( "pants_damage_multiplier" ).comment( "Percent of damage transferred to pants." ) )
				.insertTo( this );

			this.name( "Dodge" ).comment( "Gives a chance to completely avoid any kind of damage." );
		}

		private void dodgeDamage( OnPreDamaged.Data data ) {
			spawnEffects( data.target, data.level );
			damagePants( data.target, data.damage );
			data.isCancelled = true;
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
			SoundHandler.FIRE_EXTINGUISH.play( level, entity.position() );
		}

		private void damagePants( LivingEntity entity, float damage ) {
			float multiplier = this.pantsDamageMultiplier.asFloat();
			if( multiplier > 0.0f ) {
				ItemStack pants = entity.getItemBySlot( EquipmentSlot.LEGS );
				int totalDamage = Math.max( 1, ( int )( damage * multiplier ) );
				pants.hurtAndBreak( totalDamage, entity, owner->owner.broadcastBreakEvent( EquipmentSlot.LEGS ) );
			}
		}

		private boolean tryToDodge( OnPreDamaged.Data data ) {
			return Random.tryChance( this.enchantment.get().getEnchantmentLevel( data.target ) * this.chance.get() );
		}
	}
}
