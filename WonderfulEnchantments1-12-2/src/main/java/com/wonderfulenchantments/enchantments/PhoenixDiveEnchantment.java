package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class PhoenixDiveEnchantment extends Enchantment {
	public PhoenixDiveEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
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
		return !( enchantment instanceof EnchantmentFrostWalker );
	}

	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		double distance = event.getDistance();

		if( distance > 3.0D ) {
			EntityLivingBase livingEntity = event.getEntityLiving();
			World world = livingEntity.getEntityWorld();

			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE, livingEntity.getItemStackFromSlot( EntityEquipmentSlot.FEET ) );

			if( enchantmentLevel > 0 ) {
				double range = 5.0D;
				List< Entity > entities = world.getEntitiesWithinAABBExcludingEntity( livingEntity, livingEntity.getEntityBoundingBox().offset( -range, -livingEntity.height * 0.5D, -range ).expand( range * 2.0D, 0, range * 2.0D ) );
				for( Entity e : entities )
					if( e instanceof EntityLiving ) {
						EntityLivingBase entity = ( EntityLiving )e;
						entity.attackEntityFrom( DamageSource.causeExplosionDamage( livingEntity ), ( float )Math.sqrt( enchantmentLevel * distance ) );
						entity.setFire( 20 * ( 2 * enchantmentLevel ) );
					}
			}
		}
	}

	@SubscribeEvent
	public static void onJump( LivingEvent.LivingJumpEvent event ) {
		EntityLivingBase entity = event.getEntityLiving();

		if( entity instanceof EntityPlayer ) {
			EntityPlayer player = ( EntityPlayer )entity;
			ItemStack boots = entity.getItemStackFromSlot( EntityEquipmentSlot.FEET );
			int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.PHOENIX_DIVE, boots );

			if( player.isSneaking() && enchantmentLevel > 0 ) {
				player.setVelocity( player.motionX, player.motionY * ( 1.0D + ( enchantmentLevel + 1 ) * 0.25D ), player.motionZ );
				boots.damageItem( 3, player );
			}
		}
	}
}
