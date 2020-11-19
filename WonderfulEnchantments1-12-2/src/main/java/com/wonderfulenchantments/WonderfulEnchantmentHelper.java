package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class WonderfulEnchantmentHelper {
	public static final int disableEnchantmentValue = 9001;

	public static < InstanceType > int calculateEnchantmentSumIfIsInstanceOf( Enchantment enchantment, EntityLivingBase entityLivingBase, EntityEquipmentSlot[] slotTypes, Class< InstanceType > type ) {
		int sum = 0;

		for( EntityEquipmentSlot slotType : slotTypes ) {
			ItemStack itemStack = entityLivingBase.getItemStackFromSlot( slotType );
			if( type.isInstance( itemStack.getItem() ) )
				sum += EnchantmentHelper.getEnchantmentLevel( enchantment, itemStack );
		}

		return sum;
	}

	public static int calculateEnchantmentSum( Enchantment enchantment, EntityLivingBase entityLivingBase, EntityEquipmentSlot[] slotTypes ) {
		int sum = 0;

		for( EntityEquipmentSlot slotType : slotTypes )
			sum += EnchantmentHelper.getEnchantmentLevel( enchantment, entityLivingBase.getItemStackFromSlot( slotType ) );

		return sum;
	}

	public static int increaseLevelIfEnchantmentIsDisabled( Enchantment enchantment ) {
		if( enchantment instanceof FanaticEnchantment )
			return ( ConfigHandler.enchantments.FISHING_FANATIC ? 0 : disableEnchantmentValue );

		if( enchantment instanceof HumanSlayerEnchantment )
			return ( ConfigHandler.enchantments.HUMAN_SLAYER ? 0 : disableEnchantmentValue );

		if( enchantment instanceof DodgeEnchantment )
			return ( ConfigHandler.enchantments.DODGE ? 0 : disableEnchantmentValue );

		if( enchantment instanceof EnlightenmentEnchantment )
			return ( ConfigHandler.enchantments.ENLIGHTENMENT ? 0 : disableEnchantmentValue );

		if( enchantment instanceof VitalityEnchantment )
			return ( ConfigHandler.enchantments.VITALITY ? 0 : disableEnchantmentValue );

		if( enchantment instanceof PhoenixDiveEnchantment )
			return ( ConfigHandler.enchantments.PHOENIX_DIVE ? 0 : disableEnchantmentValue );

		if( enchantment instanceof PufferfishVengeanceEnchantment )
			return ( ConfigHandler.enchantments.PUFFERFISH_VENGEANCE ? 0 : disableEnchantmentValue );

		if( enchantment instanceof ImmortalityEnchantment )
			return ( ConfigHandler.enchantments.IMMORTALITY ? 0 : disableEnchantmentValue );

		if( enchantment instanceof SmelterEnchantment )
			return ( ConfigHandler.enchantments.SMELTER ? 0 : disableEnchantmentValue );

		if( enchantment instanceof SlownessCurse )
			return ( ConfigHandler.curses.SLOWNESS ? 0 : disableEnchantmentValue );

		if( enchantment instanceof FatigueCurse )
			return ( ConfigHandler.curses.FATIGUE ? 0 : disableEnchantmentValue );

		return 0;
	}
}
