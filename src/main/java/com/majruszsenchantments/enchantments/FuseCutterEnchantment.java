package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnExplosion;
import com.mlib.items.ItemHelper;
import com.mlib.math.AABBHelper;
import com.mlib.math.Range;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FuseCutterEnchantment extends CustomEnchantment {
	public FuseCutterEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.SHIELD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.minLevelCost( level->8 )
			.maxLevelCost( level->40 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< FuseCutterEnchantment > {
		static final ParticleHandler BIG_SMOKE = new ParticleHandler( ParticleTypes.LARGE_SMOKE, ParticleHandler.offset( 0.25f ), ()->0.025f );
		static final ParticleHandler SMOKE = new ParticleHandler( ParticleTypes.SMOKE, ParticleHandler.offset( 0.25f ), ()->0.025f );
		final DoubleConfig maxDistance = new DoubleConfig( 6.0, new Range<>( 1.0, 100.0 ) );
		final DoubleConfig cooldownRatio = new DoubleConfig( 1.5, new Range<>( 0.0, 10.0 ) );

		public Modifier() {
			super( Registries.FUSE_CUTTER, Registries.Modifiers.ENCHANTMENT );

			new OnExplosion.Context( this::cancelExplosion )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( this::isAnyoneBlockingWithFuseCutterNearby )
				.addConfig( this.maxDistance.name( "maximum_distance" ).comment( "Maximum distance in blocks from the explosion." ) )
				.addConfig( this.cooldownRatio.name( "cooldown_ratio" )
					.comment( "Ratio of explosion radius to disabled shield cooldown duration. (for instance 1.5 means that explosion with 2 blocks radius will disable the shield for 3 seconds)" )
				).insertTo( this );

			this.name( "FuseCutter" ).comment( "Cancels all nearby explosions whenever the player is blocking with a shield." );
		}

		private void cancelExplosion( OnExplosion.Data data ) {
			assert data.level != null;
			Vec3 position = data.explosion.getPosition().add( 0.0, 0.5, 0.0 );
			BIG_SMOKE.spawn( data.level, position, 8 * data.radius.intValue() );
			SMOKE.spawn( data.level, position, 12 * data.radius.intValue() );
			SoundHandler.FIRE_EXTINGUISH.play( data.level, position );
			data.event.setCanceled( true );
		}

		private boolean isAnyoneBlockingWithFuseCutterNearby( OnExplosion.Data data ) {
			assert data.level != null;
			Vec3 position = data.explosion.getPosition();
			for( LivingEntity livingEntity : data.level.getEntitiesOfClass( LivingEntity.class, AABBHelper.createInflatedAABB( position, this.maxDistance.get() ) ) ) {
				if( !( livingEntity instanceof ServerPlayer player ) || !livingEntity.isBlocking() )
					continue;

				ItemStack itemStack = ItemHelper.getCurrentlyUsedItem( livingEntity );
				if( this.enchantment.get().hasEnchantment( itemStack ) ) {
					itemStack.hurtAndBreak( data.radius.intValue(), player, owner->owner.broadcastBreakEvent( livingEntity.getUsedItemHand() ) );
					EntityHelper.disableCurrentItem( player, data.radius.doubleValue() * this.cooldownRatio.get() );
					return true;
				}
			}
			return false;
		}
	}
}
