package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.EffectConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import com.mlib.levels.LevelHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Supplier;

public class VampirismCurse extends CustomEnchantment {
	public VampirismCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@AutoInstance
	public static class Handler {
		final EffectConfig weakness = new EffectConfig( MobEffects.WEAKNESS, 0, 20.0 ).stackable( 120.0 );
		final EffectConfig hunger = new EffectConfig( MobEffects.HUNGER, 0, 20.0 ).stackable( 120.0 );
		final DoubleConfig fireDuration = new DoubleConfig( 5.0, new Range<>( 1.0, 60.0 ) );
		final BooleanConfig scalesWithLevel = new BooleanConfig( true );
		final Supplier< VampirismCurse > enchantment = Registries.VAMPIRISM;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Vampirism" )
				.comment( "Weakens and ignites the player when in daylight, but makes Leech enchantment stronger." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEntityTick.listen( this::applyDebuffs )
				.addCondition( Condition.< OnEntityTick.Data > cooldown( 2.0, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->LevelHelper.isEntityOutside( data.entity ) && LevelHelper.isDayAt( data.entity ) ) )
				.addConfig( this.weakness.name( "Weakness" ) )
				.addConfig( this.hunger.name( "Hunger" ) )
				.addConfig( this.fireDuration.name( "fire_duration" ).comment( "Time the player will be set on fire in seconds per enchantment level." ) )
				.addConfig( this.scalesWithLevel.name( "scales_with_level" )
					.comment( "Determines whether effects should be stronger with more cursed items equipped." )
				).insertTo( group );

			OnEntityTick.listen( this::spawnParticles )
				.addCondition( Condition.< OnEntityTick.Data > cooldown( 0.2, Dist.DEDICATED_SERVER ).configurable( false ) )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->LevelHelper.isEntityOutside( data.entity ) && LevelHelper.isDayAt( data.entity ) ) )
				.insertTo( group );

			OnPlayerInteract.listen( this::blockSleep )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( Handler::isBed ) )
				.insertTo( group );
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
			Vec3 position = AnyPos.from( data.entity.position() ).add( 0.0, data.entity.getBbHeight() * 0.5, 0.0 ).vec3();
			ParticleHandler.SMOKE.spawn( data.getServerLevel(), position, 10, ()->new Vec3( 0.25, 0.5, 0.25 ) );
		}

		private void blockSleep( OnPlayerInteract.Data data ) {
			data.event.setCancellationResult( InteractionResult.FAIL );
			data.event.setCanceled( true );
			data.player.displayClientMessage( Component.translatable( "enchantment.majruszsenchantments.vampirism_curse.block_sleep" ), true );
		}

		private static boolean isBed( OnPlayerInteract.Data data ) {
			if( data.event instanceof PlayerInteractEvent.RightClickBlock event ) {
				BlockPos position = event.getHitVec().getBlockPos();
				return data.getLevel().getBlockState( position ).isBed( data.getLevel(), position, data.player );
			}

			return false;
		}
	}
}
