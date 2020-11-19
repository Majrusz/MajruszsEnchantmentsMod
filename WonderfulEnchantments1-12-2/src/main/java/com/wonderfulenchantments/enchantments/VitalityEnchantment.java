package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "575cb29a-1ee4-11eb-adc1-0242ac120002", "VitalityBonus", SharedMonsterAttributes.MAX_HEALTH, Constants.AttributeModifierOperation.ADD );
	protected static final double healthPerLevel = 2.0D;

	public VitalityEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, EquipmentSlotTypes.BOTH_HANDS );

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
		return 5 + 8 * ( level ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 10;
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack ) {
		return ( stack.getItem() instanceof ItemShield ) && super.canApplyAtEnchantingTable( stack );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		attributeHelper.setValue( getHealthBonus( entityLivingBase ) ).apply( entityLivingBase );
	}

	protected static double getHealthBonus( EntityLivingBase entityLivingBase ) {
		int sum = WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.VITALITY, entityLivingBase, EquipmentSlotTypes.BOTH_HANDS, ItemShield.class );

		return sum * healthPerLevel;
	}
}
