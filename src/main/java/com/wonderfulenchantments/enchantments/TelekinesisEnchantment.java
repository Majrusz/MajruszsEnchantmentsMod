package com.wonderfulenchantments.enchantments;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;

/** Enchantment that puts an item directly to inventory when destroying blocks. (if possible) */
public class TelekinesisEnchantment extends WonderfulEnchantment {
	public TelekinesisEnchantment() {
		super( "telekinesis", Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, "Telekinesis" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}
}
