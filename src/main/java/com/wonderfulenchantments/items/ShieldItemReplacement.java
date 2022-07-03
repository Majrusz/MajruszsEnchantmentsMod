package com.wonderfulenchantments.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ShieldItem;

/** Makes shield enchantable. */
public class ShieldItemReplacement extends ShieldItem {
	public ShieldItemReplacement() {
		super( new Properties().durability( 336 ).tab( CreativeModeTab.TAB_COMBAT ) );
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}
}