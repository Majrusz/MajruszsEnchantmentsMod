package com.wonderfulenchantments.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

/** Enchantment that puts an item directly to inventory when destroying blocks. (if possible) */
public class TelekinesisEnchantment extends WonderfulEnchantment {
	public TelekinesisEnchantment() {
		super( "telekinesis", Rarity.UNCOMMON, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "Telekinesis" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}
}
