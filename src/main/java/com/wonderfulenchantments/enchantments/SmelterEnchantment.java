package com.wonderfulenchantments.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;

/** Enchantment that automatically smelts destroyed blocks. (if possible) */
public class SmelterEnchantment extends WonderfulEnchantment {
	public SmelterEnchantment() {
		super( Rarity.UNCOMMON, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "Smelter" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof SilkTouchEnchantment ) && super.canApplyTogether( enchantment );
	}
}
