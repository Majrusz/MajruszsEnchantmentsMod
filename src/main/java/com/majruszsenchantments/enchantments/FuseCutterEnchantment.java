package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.contexts.OnExplosionStart;
import com.mlib.items.ItemHelper;
import com.mlib.math.AABBHelper;
import com.mlib.math.Range;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class FuseCutterEnchantment extends CustomEnchantment {
	public FuseCutterEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.SHIELD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.minLevelCost( level->8 )
			.maxLevelCost( level->40 );
	}

	@AutoInstance
	public static class Handler {
		static final ParticleHandler BIG_SMOKE = new ParticleHandler( ParticleTypes.LARGE_SMOKE, ParticleHandler.offset( 0.25f ), ()->0.025f );
		static final ParticleHandler SMOKE = new ParticleHandler( ParticleTypes.SMOKE, ParticleHandler.offset( 0.25f ), ()->0.025f );
		final DoubleConfig maxDistance = new DoubleConfig( 6.0, new Range<>( 1.0, 100.0 ) );
		final DoubleConfig cooldownRatio = new DoubleConfig( 1.5, new Range<>( 0.0, 10.0 ) );
		final Supplier< FuseCutterEnchantment > enchantment = Registries.FUSE_CUTTER;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "FuseCutter" )
				.comment( "Cancels all nearby explosions whenever the player is blocking with a shield." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnExplosionStart.listen( this::cancelExplosion )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( this::isAnyoneBlockingWithFuseCutterNearby ) )
				.addConfig( this.maxDistance.name( "maximum_distance" ).comment( "Maximum distance in blocks from the explosion." ) )
				.addConfig( this.cooldownRatio.name( "cooldown_ratio" )
					.comment( "Ratio of explosion radius to disabled shield cooldown duration. (for instance 1.5 means that explosion with 2 blocks radius will disable the shield for 3 seconds)" )
				).insertTo( group );
		}

		private void cancelExplosion( OnExplosionStart.Data data ) {
			Vec3 position = data.explosion.getPosition().add( 0.0, 0.5, 0.0 );
			BIG_SMOKE.spawn( data.getServerLevel(), position, 8 * data.radius.intValue() );
			SMOKE.spawn( data.getServerLevel(), position, 12 * data.radius.intValue() );
			SoundHandler.FIRE_EXTINGUISH.play( data.getServerLevel(), position );
			data.event.setCanceled( true );
		}

		private boolean isAnyoneBlockingWithFuseCutterNearby( OnExplosionStart.Data data ) {
			Level level = data.getLevel();
			Vec3 position = data.explosion.getPosition();
			for( LivingEntity livingEntity : level.getEntitiesOfClass( LivingEntity.class, AABBHelper.createInflatedAABB( position, this.maxDistance.get() ) ) ) {
				if( !( livingEntity instanceof ServerPlayer player ) || !livingEntity.isBlocking() )
					continue;

				ItemStack itemStack = ItemHelper.getCurrentlyUsedItem( livingEntity );
				if( this.enchantment.get().hasEnchantment( itemStack ) ) {
					EntityHelper.disableCurrentItem( player, data.radius.doubleValue() * this.cooldownRatio.get() );
					return true;
				}
			}
			return false;
		}
	}
}
