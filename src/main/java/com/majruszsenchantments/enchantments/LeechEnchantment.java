package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.configs.VampirismDoubleConfig;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.math.AnyPos;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.server.level.ServerLevel;
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
	public LeechEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.MELEE_MINECRAFT )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->20 )
			.maxLevelCost( level->40 );
	}

	@AutoInstance
	public static class Handler {
		final VampirismDoubleConfig healthChance = new VampirismDoubleConfig( 0.1, 0.1 );
		final VampirismDoubleConfig hungerChance = new VampirismDoubleConfig( 0.1, 0.1 );
		final VampirismDoubleConfig effectChance = new VampirismDoubleConfig( 0.1, 0.1 );
		final Supplier< LeechEnchantment > enchantment = Registries.LEECH;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Leech" )
				.comment( "Gives a chance to steal positive effects, health and hunger points from enemies." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnDamaged.listen( this::tryToLeechAnything )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.attacker ) )
				.addCondition( OnDamaged.dealtAnyDamage() )
				.addConfig( this.healthChance.name( "HealthChance" ).comment( "Chance to steal 1 health point from the target." ) )
				.addConfig( this.hungerChance.name( "HungerChance" ).comment( "Chance to steal 1 hunger point from the target." ) )
				.addConfig( this.effectChance.name( "EffectChance" ).comment( "Chance to steal 1 random positive effect from the target." ) )
				.insertTo( group );
		}

		private void tryToLeechAnything( OnDamaged.Data data ) {
			assert data.attacker != null && data.getLevel() != null;
			boolean leechedAnything;
			leechedAnything = tryToLeech( this.healthChance, this::leechHealth, data );
			leechedAnything = tryToLeech( this.hungerChance, this::leechHunger, data ) || leechedAnything;
			leechedAnything = tryToLeech( this.effectChance, this::leechEffect, data ) || leechedAnything;

			if( leechedAnything ) {
				this.spawnEffects( data.getServerLevel(), data.attacker, data.target );
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
					MobEffectHelper.tryToApply( attacker, effect, maximumDuration, effectInstance.getAmplifier() );
					target.removeEffect( effect );
					return true;
				}
			}
			return false;
		}

		private void spawnEffects( ServerLevel level, LivingEntity attacker, LivingEntity target ) {
			Vec3 from = AnyPos.from( attacker.position() ).add( 0.0, attacker.getBbHeight() * 0.75, 0.0 ).vec3();
			Vec3 to = AnyPos.from( target.position() ).add( 0.0, target.getBbHeight() * 0.75, 0.0 ).vec3();
			ParticleHandler.ENCHANTED_HIT.spawnLine( level, from, to, 5 );

			SoundHandler.DRINK.play( level, from, SoundHandler.randomized( 0.25f ) );
		}
	}
}
