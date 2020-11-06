package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class PhoenixDiveEnchantment extends Enchantment {
	protected static List< Vec3d > positionsToGenerateParticles = new ArrayList<>();

	public PhoenixDiveEnchantment() {
		super( Rarity.RARE, EnchantmentType.ARMOR_FEET, new EquipmentSlotType[]{ EquipmentSlotType.FEET } );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + 10 * ( level );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof FrostWalkerEnchantment );
	}

	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		double distance = event.getDistance();

		if( distance > 3.0D ) {
			LivingEntity livingEntity = event.getEntityLiving();
			World world = livingEntity.getEntityWorld();

			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE.get(), livingEntity.getItemStackFromSlot( EquipmentSlotType.FEET ) );

			if( enchantmentLevel > 0 ) {
				double range = 5.0D;
				List< Entity > entities = world.getEntitiesWithinAABBExcludingEntity( livingEntity.getEntity(), livingEntity.getBoundingBox().offset( -range, -livingEntity.getHeight() * 0.5D, -range ).expand( range * 2.0D, 0, range * 2.0D ) );
				for( Entity e : entities )
					if( e instanceof LivingEntity ) {
						LivingEntity entity = ( LivingEntity )e;
						entity.attackEntityFrom( DamageSource.causeExplosionDamage( livingEntity ), ( float )Math.sqrt( enchantmentLevel * distance ) );
						entity.setFireTimer( 20 * ( 2 * enchantmentLevel ) );
					}

				positionsToGenerateParticles.add( livingEntity.getPositionVector() );
			}
		}
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.WorldTickEvent event ) {
		if( positionsToGenerateParticles.size() > 0 ) {
			for( Vec3d position : positionsToGenerateParticles )
				for( double d = 0.0D; d < 3.0D; d++ )
					( ( ServerWorld )event.world ).spawnParticle( RegistryHandler.PHOENIX_PARTICLE.get(), position.getX(), position.getY(), position.getZ(), ( int )Math.pow( 4.0D, d + 1.0D ), 0.0625D, 0.125D, 0.0625D, 0.125D * ( d + 1.0D ) );

			positionsToGenerateParticles.clear();
		}
	}

	@SubscribeEvent
	public static void onJump( LivingEvent.LivingJumpEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		if( entity instanceof PlayerEntity ) {
			PlayerEntity player = ( PlayerEntity )entity;
			ItemStack boots = entity.getItemStackFromSlot( EquipmentSlotType.FEET );
			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE.get(), boots );

			if( player.isCrouching() && enchantmentLevel > 0 ) {
				player.setMotion( player.getMotion().mul( new Vec3d( 0.0D, 1.0D + ( enchantmentLevel + 1 ) * 0.25D, 0.0D ) ) );
				boots.damageItem( 3, player, ( e )->e.sendBreakAnimation( EquipmentSlotType.FEET ) );
			}
		}
	}
}
