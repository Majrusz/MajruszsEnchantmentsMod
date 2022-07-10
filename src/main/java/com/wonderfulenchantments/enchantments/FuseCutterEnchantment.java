package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnExplosionContext;
import com.mlib.gamemodifiers.data.OnExplosionData;
import com.mlib.items.ItemHelper;
import com.mlib.math.AABBHelper;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class FuseCutterEnchantment extends CustomEnchantment {
	public static Supplier< FuseCutterEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.SHIELD, EquipmentSlots.BOTH_HANDS, false, 1, level->8, level->40 );
		FuseCutterEnchantment enchantment = new FuseCutterEnchantment( params );
		Modifier modifier = new FuseCutterEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public FuseCutterEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< FuseCutterEnchantment > {
		final DoubleConfig maxDistance = new DoubleConfig( "maximum_distance", "Maximum distance in blocks from the explosion.", false, 6.0, 1.0, 100.0 );

		public Modifier( FuseCutterEnchantment enchantment ) {
			super( enchantment, "FuseCutter", "Cancels all nearby explosions whenever the player is blocking with a shield." );

			OnExplosionContext onExplosion = new OnExplosionContext( this::cancelExplosion );
			onExplosion.addCondition( data->data.level != null && this.isAnyoneBlockingWithFuseCutterNearby( data.level, data ) );

			this.addConfig( this.maxDistance );
			this.addContexts( onExplosion );
		}

		private void cancelExplosion( OnExplosionData data ) {
			assert data.level != null;
			Vec3 position = data.explosion.getPosition();
			for( int i = 0; i < 2; ++i ) {
				data.level.sendParticles( i == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, position.x, position.y + 0.5, position.z, 32 * i, 0.25, 0.25, 0.25, 0.025 );
			}
			data.level.playSound( null, position.x, position.y, position.z, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.AMBIENT, 1.0f, 1.0f );
			data.event.setCanceled( true );
		}

		private boolean isAnyoneBlockingWithFuseCutterNearby( Level level, OnExplosionData data ) {
			Vec3 position = data.explosion.getPosition();
			for( LivingEntity livingEntity : level.getEntitiesOfClass( LivingEntity.class, AABBHelper.createInflatedAABB( position, this.maxDistance.get() ) ) ) {
				if( !( livingEntity instanceof ServerPlayer player ) || !livingEntity.isBlocking() )
					continue;

				ItemStack itemStack = ItemHelper.getCurrentlyUsedItem( livingEntity );
				if( this.enchantment.hasEnchantment( itemStack ) ) {
					itemStack.hurtAndBreak( data.radius.intValue(), player, owner->owner.broadcastBreakEvent( livingEntity.getUsedItemHand() ) );
					player.disableShield( true );
					return true;
				}
			}
			return false;
		}
	}
}
