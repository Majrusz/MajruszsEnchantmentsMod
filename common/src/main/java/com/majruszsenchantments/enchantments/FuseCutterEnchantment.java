package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnExploded;
import com.mlib.contexts.base.Condition;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.emitter.SoundEmitter;
import com.mlib.entity.EntityHelper;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.ItemHelper;
import com.mlib.math.AnyPos;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@AutoInstance
public class FuseCutterEnchantment extends Handler {
	float maxDistance = 6.0f;
	float cooldownRatio = 1.5f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( MajruszsEnchantments.IS_SHIELD )
			.slots( EquipmentSlots.HANDS )
			.minLevelCost( level->8 )
			.maxLevelCost( level->40 );
	}

	public FuseCutterEnchantment() {
		super( MajruszsEnchantments.FUSE_CUTTER, false );

		OnExploded.listen( this::cancel )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( this::isAnyoneBlockingNearby );

		this.config.defineFloat( "max_distance", s->this.maxDistance, ( s, v )->this.maxDistance = Range.of( 1.0f, 64.0f ).clamp( v ) );
		this.config.defineFloat( "shield_cooldown_ratio", s->this.cooldownRatio, ( s, v )->this.cooldownRatio = Range.of( 0.0f, 10.0f ).clamp( v ) );
	}

	private void cancel( OnExploded data ) {
		Vec3 position = AnyPos.from( data.position ).add( 0.0, 0.5, 0.0 ).vec3();

		data.cancelExplosion();

		ParticleEmitter.of( ParticleTypes.SMOKE )
			.offset( ParticleEmitter.offset( 0.125f * data.radius ) )
			.speed( 0.025f )
			.count( Random.round( 12 * data.radius ) )
			.position( position )
			.emit( data.getServerLevel() );

		ParticleEmitter.of( ParticleTypes.LARGE_SMOKE )
			.offset( ParticleEmitter.offset( 0.125f * data.radius ) )
			.speed( 0.025f )
			.count( Random.round( 8 * data.radius ) )
			.position( position )
			.emit( data.getServerLevel() );

		SoundEmitter.of( SoundEvents.FIRE_EXTINGUISH )
			.position( position )
			.emit( data.getServerLevel() );
	}

	private boolean isAnyoneBlockingNearby( OnExploded data ) {
		AABB aabb = AABB.unitCubeFromLowerCorner( data.position ).inflate( this.maxDistance );
		for( LivingEntity entity : data.getLevel().getEntitiesOfClass( LivingEntity.class, aabb ) ) {
			if( !( entity instanceof ServerPlayer player ) || !entity.isBlocking() ) {
				continue;
			}

			if( EnchantmentHelper.has( this.enchantment, ItemHelper.getCurrentlyUsedItem( entity ) ) ) {
				EntityHelper.disableCurrentItem( player, data.radius * this.cooldownRatio );
				return true;
			}
		}

		return false;
	}
}
