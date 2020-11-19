package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemShield;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SlownessCurse extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "760f7b82-76c7-4875-821e-ef0579b881e0", "SlownessCurse", SharedMonsterAttributes.MOVEMENT_SPEED, Constants.AttributeModifierOperation.MULTIPLY );
	protected static final float slownessMultiplierPerLevel = 0.125f;

	public SlownessCurse( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 40;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		attributeHelper.setValue( getTotalSlownessMultiplier( entityLivingBase ) ).apply( entityLivingBase );
	}

	private static float getTotalSlownessMultiplier( EntityLivingBase entityLivingBase ) {
		int sum = 0;

		sum += WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.SLOWNESS, entityLivingBase, EquipmentSlotTypes.ARMOR );
		sum += WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.SLOWNESS, entityLivingBase, EquipmentSlotTypes.BOTH_HANDS, ItemShield.class );

		return -( ( float )( sum ) * slownessMultiplierPerLevel );
	}
}