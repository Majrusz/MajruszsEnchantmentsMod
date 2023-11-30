package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.events.OnEntityDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.time.TimeHelper;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

@AutoInstance
public class LeechEnchantment extends Handler {
	float healthChance = 0.1f;
	float hungerChance = 0.1f;
	float effectChance = 0.1f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_MELEE_MINECRAFT )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->20 )
			.maxLevelCost( level->40 );
	}

	public LeechEnchantment() {
		super( MajruszsEnchantments.LEECH, LeechEnchantment.class, false );

		OnEntityDamaged.listen( this::tryToLeechAnything )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.attacker ) );

		this.config.define( "health_chance", Reader.number(), s->this.healthChance, ( s, v )->this.healthChance = Range.CHANCE.clamp( v ) )
			.define( "hunger_chance", Reader.number(), s->this.hungerChance, ( s, v )->this.hungerChance = Range.CHANCE.clamp( v ) )
			.define( "effect_chance", Reader.number(), s->this.effectChance, ( s, v )->this.effectChance = Range.CHANCE.clamp( v ) );
	}

	private void tryToLeechAnything( OnEntityDamaged data ) {
		boolean leechedAnything;
		leechedAnything = this.tryToLeech( this.healthChance, this::leechHealth, data );
		leechedAnything = this.tryToLeech( this.hungerChance, this::leechHunger, data ) || leechedAnything;
		leechedAnything = this.tryToLeech( this.effectChance, this::leechEffect, data ) || leechedAnything;

		if( leechedAnything ) {
			this.spawnEffects( data );
		}
	}

	private boolean tryToLeech( float chance, Predicate< OnEntityDamaged > predicate, OnEntityDamaged data ) {
		return Random.check( chance )
			&& predicate.test( data );
	}

	private boolean leechHealth( OnEntityDamaged data ) {
		data.target.hurt( data.getLevel().damageSources().magic(), 1.0f );
		data.attacker.heal( 1.0f );

		return true;
	}

	private boolean leechHunger( OnEntityDamaged data ) {
		if( data.attacker instanceof Player playerAttacker ) {
			FoodData attackerFood = playerAttacker.getFoodData();
			attackerFood.setFoodLevel( Math.min( attackerFood.getFoodLevel() + 1, 20 ) );
			if( data.target instanceof Player playerTarget ) {
				FoodData targetFood = playerTarget.getFoodData();
				targetFood.setFoodLevel( Math.max( targetFood.getFoodLevel() - 1, 0 ) );
			}
			return true;
		}

		return false;
	}

	private boolean leechEffect( OnEntityDamaged data ) {
		for( MobEffectInstance effectInstance : data.target.getActiveEffects() ) {
			MobEffect effect = effectInstance.getEffect();
			if( effect.isBeneficial() ) {
				int duration = Math.min( TimeHelper.toTicks( 30.0 ), effectInstance.getDuration() );
				data.attacker.addEffect( new MobEffectInstance( effect, duration, effectInstance.getAmplifier() ) );
				data.target.removeEffect( effect );

				return true;
			}
		}

		return false;
	}

	private void spawnEffects( OnEntityDamaged data ) {
		Vec3 from = AnyPos.from( data.attacker.position() ).add( 0.0, data.attacker.getBbHeight() * 0.75, 0.0 ).vec3();
		Vec3 to = AnyPos.from( data.target.position() ).add( 0.0, data.target.getBbHeight() * 0.75, 0.0 ).vec3();

		ParticleEmitter.of( ParticleTypes.ENCHANTED_HIT )
			.count( Random.round( AnyPos.from( from ).dist( to ).floatValue() * 5.0f ) )
			.offset( ParticleEmitter.offset( 0.05f ) )
			.position( from )
			.emitLine( data.getServerLevel(), to );

		SoundEmitter.of( SoundEvents.GENERIC_DRINK )
			.volume( SoundEmitter.randomized( 0.25f ) )
			.position( from )
			.emit( data.getServerLevel() );
	}
}
