package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.configs.EffectConfig;
import com.mlib.gamemodifiers.configs.StackableEffectConfig;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import com.mlib.levels.LevelHelper;
import com.mlib.math.Range;
import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class VampirismCurse extends CustomEnchantment {
	public VampirismCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< VampirismCurse > {
		final EffectConfig weakness = new StackableEffectConfig( MobEffects.WEAKNESS, 0, 20.0, 120.0 );
		final EffectConfig hunger = new StackableEffectConfig( MobEffects.HUNGER, 0, 20.0, 120.0 );
		final DoubleConfig fireDuration = new DoubleConfig( 5.0, new Range<>( 1.0, 60.0 ) );
		final BooleanConfig scalesWithLevel = new BooleanConfig( true );

		public Modifier() {
			super( Registries.VAMPIRISM, Registries.Modifiers.CURSE );

			new OnEntityTick.Context( this::applyDebuffs )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 2.0, Dist.DEDICATED_SERVER ).setConfigurable( false ) )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( data->LevelHelper.isEntityOutsideDuringTheDay( data.entity ) )
				.addConfig( this.weakness.name( "Weakness" ) )
				.addConfig( this.hunger.name( "Hunger" ) )
				.addConfig( this.fireDuration.name( "fire_duration" ).comment( "Time the player will be set on fire in seconds per enchantment level." ) )
				.addConfig( this.scalesWithLevel.name( "scales_with_level" )
					.comment( "Determines whether effects should be stronger with more cursed items equipped." )
				).insertTo( this );

			new OnEntityTick.Context( this::spawnParticles )
				.name( "Particles" )
				.addCondition( new Condition.Cooldown< OnEntityTick.Data >( 0.2, Dist.DEDICATED_SERVER ).setConfigurable( false ) )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( data->LevelHelper.isEntityOutsideDuringTheDay( data.entity ) )
				.insertTo( this );

			new OnPlayerInteract.Context( this::blockSleep )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( Modifier::isBedCondition )
				.insertTo( this );

			this.name( "Vampirism" ).comment( "Weakens and ignites the player when in daylight, but makes Leech enchantment stronger." );
		}

		private void applyDebuffs( OnEntityTick.Data data ) {
			assert data.entity != null;
			int enchantmentSum = this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
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

		private void spawnParticles( OnEntityTick.Data data ) {
			assert data.entity != null && data.level != null;
			Vec3 position = VectorHelper.add( data.entity.position(), new Vec3( 0.0, data.entity.getBbHeight() * 0.5, 0.0 ) );
			ParticleHandler.SMOKE.spawn( data.level, position, 10, ()->new Vec3( 0.25, 0.5, 0.25 ) );
		}

		private void blockSleep( OnPlayerInteract.Data data ) {
			data.event.setCancellationResult( InteractionResult.FAIL );
			data.event.setCanceled( true );
			data.player.displayClientMessage( Component.translatable( "enchantment.majruszsenchantments.vampirism_curse.block_sleep" ), true );
		}

		private static boolean isBedCondition( OnPlayerInteract.Data data ) {
			assert data.level != null;
			if( data.event instanceof PlayerInteractEvent.RightClickBlock event ) {
				BlockPos position = event.getHitVec().getBlockPos();
				return data.level.getBlockState( position ).isBed( data.level, position, data.player );
			}

			return false;
		}
	}
}
