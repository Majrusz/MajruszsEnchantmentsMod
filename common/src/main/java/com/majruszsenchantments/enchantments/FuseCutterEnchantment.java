package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnExploded;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
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
		super( MajruszsEnchantments.FUSE_CUTTER, FuseCutterEnchantment.class, false );

		OnExploded.listen( this::cancel )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( this::isAnyoneBlockingNearby );

		this.config.define( "max_distance", Reader.number(), s->this.maxDistance, ( s, v )->this.maxDistance = Range.of( 1.0f, 64.0f ).clamp( v ) )
			.define( "shield_cooldown_ratio", Reader.number(), s->this.cooldownRatio, ( s, v )->this.cooldownRatio = Range.of( 0.0f, 10.0f ).clamp( v ) );
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
