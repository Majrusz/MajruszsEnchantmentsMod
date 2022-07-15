package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.data.OnEntityTickData;
import com.mlib.gamemodifiers.parameters.ContextParameters;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.levels.LevelHelper;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;

import java.util.function.Supplier;

public class VampirismCurse extends CustomEnchantment {
	public static Supplier< VampirismCurse > create() {
		Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, true, 1, level->10, level->50 );
		VampirismCurse enchantment = new VampirismCurse( params );
		Modifier modifier = new VampirismCurse.Modifier( enchantment );

		return ()->enchantment;
	}

	public VampirismCurse( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< VampirismCurse > {
		final EffectConfig weakness = new EffectConfig( "Weakness", ()->MobEffects.WEAKNESS, 0, 20.0, 120.0 );
		final EffectConfig hunger = new EffectConfig( "Hunger", ()->MobEffects.HUNGER, 0, 20.0, 120.0 );
		final DoubleConfig fireDuration = new DoubleConfig( "fire_duration", "Time the player will be set on fire in seconds per enchantment level.", false, 5.0, 1.0, 60.0 );
		final BooleanConfig scalesWithLevel = new BooleanConfig( "scales_with_level", "Determines whether effects should be stronger with more cursed items equipped.", false, true );

		public Modifier( VampirismCurse enchantment ) {
			super( enchantment, "Vampirism", "Weakens and ignites the player when in daylight, but makes Leech enchantment stronger." );

			OnEntityTickContext onTick = new OnEntityTickContext( this::applyDebuffs, new ContextParameters( Priority.NORMAL, "Debuffs", "" ) );
			onTick.addCondition( new Condition.Cooldown( 2.0, Dist.DEDICATED_SERVER ) )
				.addCondition( new Condition.HasEnchantment( enchantment ) )
				.addCondition( data->data.level != null )
				.addCondition( data->LevelHelper.isEntityOutsideDuringTheDay( data.entity ) )
				.addConfigs( this.weakness, this.hunger, this.fireDuration, this.scalesWithLevel );

			OnEntityTickContext onTick2 = new OnEntityTickContext( this::spawnParticles, new ContextParameters( Priority.NORMAL, "Particles", "" ) );
			onTick2.addCondition( new Condition.Cooldown( 0.2, Dist.DEDICATED_SERVER ) )
				.addCondition( new Condition.HasEnchantment( enchantment ) )
				.addCondition( data->data.level != null )
				.addCondition( data->LevelHelper.isEntityOutsideDuringTheDay( data.entity ) );

			this.addContexts( onTick, onTick2 );
		}

		private void applyDebuffs( OnEntityTickData data ) {
			assert data.entity != null;
			int enchantmentSum = this.enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			setOnFire( enchantmentSum, data.entity );
			applyEffects( enchantmentSum, data.entity );
		}

		private void setOnFire( int enchantmentSum, LivingEntity entity ) {
			entity.setSecondsOnFire( ( int )( this.fireDuration.get() * enchantmentSum ) );
		}

		private void applyEffects( int enchantmentSum, LivingEntity entity ) {
			int extraAmplifier = this.scalesWithLevel.isEnabled() ? enchantmentSum - 1 : 0;
			this.weakness.apply( entity, extraAmplifier, 0 );
			this.hunger.apply( entity, extraAmplifier, 0 );
		}

		private void spawnParticles( OnEntityTickData data ) {
			assert data.entity != null && data.level != null;
			Vec3 position = data.entity.position();
			data.level.sendParticles( ParticleTypes.SMOKE, position.x(), data.entity.getY( 0.5 ), position.z(), 10, 0.25, 0.5, 0.25, 0.01 );
		}
	}
}
