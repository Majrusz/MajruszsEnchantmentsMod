package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.configs.VampirismDoubleConfig;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.effects.EffectHelper;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamaged;
import com.mlib.math.VectorHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LeechEnchantment extends CustomEnchantment {
	public static Supplier< LeechEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.MELEE_MINECRAFT, EquipmentSlots.MAINHAND, false, 1, level->20, level->40 );
		LeechEnchantment enchantment = new LeechEnchantment( params );
		Modifier modifier = new LeechEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public LeechEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< LeechEnchantment > {
		final VampirismDoubleConfig healthChance = new VampirismDoubleConfig( "HealthChance", "Chance to steal 1 health point from the target.", 0.1, 0.1 );
		final VampirismDoubleConfig hungerChance = new VampirismDoubleConfig( "HungerChance", "Chance to steal 1 hunger point from the target.", 0.1, 0.1 );
		final VampirismDoubleConfig effectChance = new VampirismDoubleConfig( "EffectChance", "Chance to steal 1 random positive effect from the target.", 0.1, 0.1 );

		public Modifier( LeechEnchantment enchantment ) {
			super( enchantment, "Leech", "Gives a chance to steal positive effects, health and hunger points from enemies." );

			OnDamaged.Context onDamaged = new OnDamaged.Context( this::tryToLeechAnything );
			onDamaged.addCondition( new Condition.IsServer() )
				.addCondition( data->data.attacker != null && enchantment.hasEnchantment( data.attacker ) )
				.addCondition( OnDamaged.DEALT_ANY_DAMAGE );

			this.addConfigs( this.healthChance, this.hungerChance, this.effectChance );
			this.addContext( onDamaged );
		}

		private void tryToLeechAnything( OnDamaged.Data data ) {
			assert data.attacker != null && data.level != null;
			boolean leechedAnything;
			leechedAnything = tryToLeech( this.healthChance, this::leechHealth, data );
			leechedAnything = tryToLeech( this.hungerChance, this::leechHunger, data ) || leechedAnything;
			leechedAnything = tryToLeech( this.effectChance, this::leechEffect, data ) || leechedAnything;

			if( leechedAnything ) {
				spawnEffects( data.level, data.attacker, data.target );
			}
		}

		private boolean tryToLeech( VampirismDoubleConfig chanceConfig, BiFunction< LivingEntity, LivingEntity, Boolean > function, OnDamaged.Data data ) {
			return Random.tryChance( chanceConfig.getTotalChance( data.attacker ) ) ? function.apply( data.attacker, data.target ) : false;
		}

		private boolean leechHealth( LivingEntity attacker, LivingEntity target ) {
			target.hurt( DamageSource.MAGIC, 1.0f );
			attacker.heal( 1.0f );
			return true;
		}

		private boolean leechHunger( LivingEntity attacker, LivingEntity target ) {
			if( attacker instanceof Player playerAttacker ) {
				FoodData attackerFood = playerAttacker.getFoodData();
				attackerFood.setFoodLevel( Math.min( attackerFood.getFoodLevel() + 1, 20 ) );
				if( target instanceof Player playerTarget ) {
					FoodData targetFood = playerTarget.getFoodData();
					targetFood.setFoodLevel( Math.max( targetFood.getFoodLevel() - 1, 0 ) );
				}
				return true;
			}
			return false;
		}

		private boolean leechEffect( LivingEntity attacker, LivingEntity target ) {
			for( MobEffectInstance effectInstance : target.getActiveEffects() ) {
				MobEffect effect = effectInstance.getEffect();
				if( effect.isBeneficial() ) {
					int maximumDuration = Math.min( Utility.secondsToTicks( 30.0 ), effectInstance.getDuration() );
					EffectHelper.applyEffectIfPossible( attacker, effect, maximumDuration, effectInstance.getAmplifier() );
					target.removeEffect( effect );
					return true;
				}
			}
			return false;
		}

		private void spawnEffects( ServerLevel level, LivingEntity attacker, LivingEntity target ) {
			Vec3 startPosition = VectorHelper.add( attacker.position(), new Vec3( 0.0, attacker.getBbHeight() * 0.75, 0.0 ) );
			Vec3 endPosition = VectorHelper.add( target.position(), new Vec3( 0.0, target.getBbHeight() * 0.75, 0.0 ) );
			Vec3 difference = VectorHelper.subtract( endPosition, startPosition );
			int amountOfParticles = ( int )( Math.ceil( startPosition.distanceTo( endPosition ) * 5.0 ) );
			for( int i = 0; i <= amountOfParticles; i++ ) {
				Vec3 stepPosition = VectorHelper.add( startPosition, VectorHelper.multiply( difference, ( float )( i ) / amountOfParticles ) );
				level.sendParticles( ParticleTypes.ENCHANTED_HIT, stepPosition.x, stepPosition.y, stepPosition.z, 1, 0.0, 0.0, 0.0, 0.0 );
			}

			SoundHandler.DRINK.play( level, startPosition, SoundHandler.randomized( 0.25f ) );
		}
	}
}
