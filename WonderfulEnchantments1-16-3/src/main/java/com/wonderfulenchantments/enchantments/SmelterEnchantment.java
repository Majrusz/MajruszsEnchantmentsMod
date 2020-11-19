package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SmelterEnchantment extends Enchantment {
	public SmelterEnchantment() {
		super( Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 8 * ( level ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 16;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof SilkTouchEnchantment ) && super.canApplyTogether( enchantment );
	}
}
