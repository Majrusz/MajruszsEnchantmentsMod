package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.EquipmentSlotTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

public class IncompatibilityCurse extends Enchantment {
	public IncompatibilityCurse() {
		super( Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 40;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return false;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return true;
	}
}
