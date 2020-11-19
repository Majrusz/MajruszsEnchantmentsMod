package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.AttributeHelper;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnegative;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class DodgeEnchantment extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "ad3e064e-e9f6-4747-a86b-46dc4e2a1444", "KnockBackImmunityTime", SharedMonsterAttributes.KNOCKBACK_RESISTANCE, Constants.AttributeModifierOperation.ADD );
	protected static HashMap< Integer, Integer > immunitiesLeft = new HashMap<>(); // holding pair (entityID, ticks left)

	public DodgeEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{ EntityEquipmentSlot.LEGS } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 14 * ( level ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingDamageEvent event ) {
		EntityLivingBase entityLivingBase = event.getEntityLiving();
		ItemStack pants = entityLivingBase.getItemStackFromSlot( EntityEquipmentSlot.LEGS );
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.DODGE, entityLivingBase );

		if( enchantmentLevel > 0 ) {
			if( !( WonderfulEnchantments.RANDOM.nextDouble() < ( double )enchantmentLevel * 0.125D ) )
				return;

			spawnParticlesAndPlaySounds( entityLivingBase );
			pants.damageItem( ( int )event.getAmount(), entityLivingBase );
			setImmunity( entityLivingBase, 50 * enchantmentLevel );

			event.setCanceled( true );
		}
	}

	@SubscribeEvent
	public static void updateEntitiesKnockBackImmunity( TickEvent.WorldTickEvent event ) {
		for( Map.Entry< Integer, Integer > pair : immunitiesLeft.entrySet() ) {
			Entity entity = event.world.getEntityByID( pair.getKey() );

			if( entity instanceof EntityLivingBase )
				updateImmunity( ( EntityLivingBase )entity );

			pair.setValue( Math.max( pair.getValue() - 1, 0 ) );
		}

		immunitiesLeft.values().removeIf( value->( value == 0 ) );
	}

	protected static void setImmunity( EntityLivingBase entityLivingBase, @Nonnegative int ticks ) {
		immunitiesLeft.put( entityLivingBase.getEntityId(), ticks );

		updateImmunity( entityLivingBase );
	}

	protected static void updateImmunity( EntityLivingBase entityLivingBase ) {
		double immunity = ( immunitiesLeft.get( entityLivingBase.getEntityId() ) > 0 ) ? 1.0D : 0.0D;

		attributeHelper.setValue( immunity ).apply( entityLivingBase );
	}

	protected static void spawnParticlesAndPlaySounds( EntityLivingBase entityLivingBase ) {
		WorldServer world = ( WorldServer )entityLivingBase.getEntityWorld();
		for( double d = 0.0D; d < 3.0D; d++ ) {
			world.spawnParticle( EnumParticleTypes.SMOKE_NORMAL, entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.height * ( 0.25D * ( d + 1.0D ) ), entityLivingBase.posZ, 32, 0.125D, 0.0D, 0.125D, 0.075D );
			world.spawnParticle( EnumParticleTypes.SMOKE_LARGE, entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.height * ( 0.25D * ( d + 1.0D ) ), entityLivingBase.posZ, 16, 0.125D, 0.0D, 0.125D, 0.025D );
		}
		world.playSound( null, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.AMBIENT, 1.0F, 1.0F );
	}
}
