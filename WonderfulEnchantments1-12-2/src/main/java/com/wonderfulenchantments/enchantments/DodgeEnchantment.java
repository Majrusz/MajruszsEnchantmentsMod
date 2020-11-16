package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
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

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class DodgeEnchantment extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "ad3e064e-e9f6-4747-a86b-46dc4e2a1444" );
	protected static final String MODIFIER_NAME = "KnockBackImmunityTime";
	protected static HashMap< String, Integer > modifiers = new HashMap<>();

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
		return 14 * ( level ) + ( ConfigHandler.Enchantments.DODGE ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingDamageEvent event ) {
		EntityLivingBase entityLiving = event.getEntityLiving();
		ItemStack pants = entityLiving.getItemStackFromSlot( EntityEquipmentSlot.LEGS );
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.DODGE, entityLiving );

		if( enchantmentLevel > 0 ) {
			if( !( WonderfulEnchantments.RANDOM.nextDouble() < ( double )enchantmentLevel * 0.125D ) )
				return;

			WorldServer world = ( WorldServer )entityLiving.getEntityWorld();
			for( double d = 0.0D; d < 3.0D; d++ ) {
				world.spawnParticle( EnumParticleTypes.SMOKE_NORMAL, entityLiving.posX, entityLiving.posY + entityLiving.height * ( 0.25D * ( d + 1.0D ) ), entityLiving.posZ, 32, 0.125D, 0.0D, 0.125D, 0.075D );
				world.spawnParticle( EnumParticleTypes.SMOKE_LARGE, entityLiving.posX, entityLiving.posY + entityLiving.height * ( 0.25D * ( d + 1.0D ) ), entityLiving.posZ, 16, 0.125D, 0.0D, 0.125D, 0.025D );
			}
			world.playSound( null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.AMBIENT, 1.0F, 1.0F );

			pants.damageItem( ( int )event.getAmount(), entityLiving );
			if( entityLiving instanceof EntityPlayer )
				setImmunity( ( EntityPlayer )( entityLiving ), 50 * enchantmentLevel );

			event.setCanceled( true );
		}
	}

	@SubscribeEvent
	public static void checkPlayersKnockBackImmunity( TickEvent.PlayerTickEvent event ) {
		EntityPlayer player = event.player;
		String nickname = player.getDisplayName().getUnformattedText();

		if( !modifiers.containsKey( nickname ) )
			modifiers.put( nickname, 0 );

		applyImmunity( player );

		modifiers.replace( nickname, Math.max( modifiers.get( nickname ) - 1, 0 ) );
	}

	private static void setImmunity( EntityPlayer player, int ticks ) {
		String nickname = player.getDisplayName().getUnformattedText();

		if( !modifiers.containsKey( nickname ) )
			modifiers.put( nickname, 0 );

		modifiers.replace( nickname, ticks );

		applyImmunity( player );
	}

	private static void applyImmunity( EntityPlayer player ) {
		String nickname = player.getDisplayName().getUnformattedText();

		IAttributeInstance resistance = player.getEntityAttribute( SharedMonsterAttributes.KNOCKBACK_RESISTANCE );
		resistance.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, ( modifiers.get( nickname ) > 0 ) ? 1.0D : 0.0D, Constants.AttributeModifierOperation.ADD );
		resistance.applyModifier( modifier );
	}
}
