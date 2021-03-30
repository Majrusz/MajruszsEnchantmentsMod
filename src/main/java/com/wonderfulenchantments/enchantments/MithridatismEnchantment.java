package com.wonderfulenchantments.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

/** Enchantment that decreases damage from poison and increases armor for each negative effect applied to the player. */
public class MithridatismEnchantment extends WonderfulEnchantment {
	public MithridatismEnchantment() {
		super( "mithridatism", Rarity.VERY_RARE, EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST, "Mithridatism" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}
}
