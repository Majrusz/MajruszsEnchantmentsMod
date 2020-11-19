package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class PhoenixDiveEnchantment extends Enchantment {
	protected static List< Vec3d > positionsToGenerateParticles = new ArrayList<>();
	protected static HashMap< Integer, Integer > particleTimers = new HashMap<>(); // holding pair (entityID, ticks since last creating particle)

	public PhoenixDiveEnchantment() {
		super( Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{ EquipmentSlotType.FEET } );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 * ( level + 1 ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof FrostWalkerEnchantment ) && super.canApplyTogether( enchantment );
	}

	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		double distance = event.getDistance();

		if( distance > 3.0D ) {
			LivingEntity attacker = event.getEntityLiving();
			World world = attacker.getEntityWorld();

			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE.get(), attacker.getItemStackFromSlot( EquipmentSlotType.FEET ) );

			if( enchantmentLevel > 0 ) {
				double range = 5.0D;
				List< Entity > entities = world.getEntitiesWithinAABBExcludingEntity( attacker.getEntity(), attacker.getBoundingBox().offset( -range, -attacker.getHeight() * 0.5D, -range ).expand( range * 2.0D, 0, range * 2.0D ) );
				for( Entity entity : entities )
					if( entity instanceof LivingEntity ) {
						LivingEntity target = ( LivingEntity )entity;
						target.attackEntityFrom( DamageSource.causeExplosionDamage( attacker ), 0 );
						target.attackEntityFrom( DamageSource.ON_FIRE, ( float )Math.sqrt( enchantmentLevel * distance ) );
						target.setFireTimer( 20 * ( 2 * enchantmentLevel ) );
					}

				positionsToGenerateParticles.add( attacker.getPositionVector() );
			}
		}
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( positionsToGenerateParticles.size() > 0 ) {
			for( Vec3d position : positionsToGenerateParticles )
				for( double d = 0.0D; d < 3.0D; d++ ) {
					ServerWorld world = ( ServerWorld )event.world;
					world.spawnParticle( RegistryHandler.PHOENIX_PARTICLE.get(), position.getX(), position.getY(), position.getZ(), ( int )Math.pow( 5.0D, d + 1.0D ), 0.0625D, 0.125D, 0.0625D, ( 0.125D + 0.0625D ) * ( d + 1.0D ) );
					world.playSound( null, position.getX(), position.getY(), position.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.AMBIENT, 0.25F, 1.0F );
				}

			positionsToGenerateParticles.clear();
		}

		for( Map.Entry< Integer, Integer > pair : particleTimers.entrySet() ) {
			Entity entity = event.world.getEntityByID( pair.getKey() );

			int ticks = pair.getValue() + 1;
			if( entity != null && ticks > 3 ) {
				ticks -= 3;
				spawnFootParticle( entity );
			}
			pair.setValue( Math.max( ticks, 0 ) );
		}

		for( Map.Entry< Integer, Integer > pair : particleTimers.entrySet() )
			if( event.world.getEntityByID( pair.getKey() ) == null )
				particleTimers.values().remove( pair.getKey() );
	}

	@SubscribeEvent
	public static void onJump( LivingEvent.LivingJumpEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		if( entity instanceof PlayerEntity ) {
			PlayerEntity player = ( PlayerEntity )entity;
			ItemStack boots = entity.getItemStackFromSlot( EquipmentSlotType.FEET );
			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE.get(), boots );

			if( player.isCrouching() && enchantmentLevel > 0 ) {
				double angleInRadians = Math.toRadians( player.rotationYaw + 90.0D );
				double factor = ( enchantmentLevel + 1 ) * 0.33334D;
				player.setMotion( player.getMotion().mul( new Vec3d( 0.0D, 1.0D + factor, 0.0D ) ).add( factor * Math.cos( angleInRadians ), 0.0D, factor * Math.sin( angleInRadians ) ) );

				boots.damageItem( 3, player, ( e )->e.sendBreakAnimation( EquipmentSlotType.FEET ) );
			}
		}
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();
		Integer entityID = livingEntity.getEntityId();

		if( EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.PHOENIX_DIVE.get(), livingEntity ) > 0 )
			particleTimers.put( entityID, 0 );
		else
			particleTimers.remove( entityID );
	}

	protected static void spawnFootParticle( Entity entity ) {
		if( entity instanceof LivingEntity ) {
			World world = entity.getEntityWorld();
			double leftLegRotation = ( WonderfulEnchantments.RANDOM.nextBoolean() ? 180.0D : 0.0D );
			double angleInRadians = Math.toRadians( entity.rotationYaw + 90.0D + leftLegRotation );
			if( world instanceof ServerWorld )
				( ( ServerWorld )world ).spawnParticle( ParticleTypes.FLAME, entity.getPosX() + 0.1875D * Math.sin( -angleInRadians ), entity.getPosY(), entity.getPosZ() + 0.1875D * Math.cos( -angleInRadians ), 1, 0.0D, 0.125D * Math.cos( angleInRadians ), 0.00D, 0.0D );
		}
	}

}
