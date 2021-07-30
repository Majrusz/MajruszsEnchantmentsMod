package com.wonderfulenchantments.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ShieldItem;

/** Shield item replacement with visible enchantability. */
public class ShieldItemReplacement extends ShieldItem {
	public ShieldItemReplacement() {
		super( ( new Properties() ).durability( 336 )
			.tab( CreativeModeTab.TAB_COMBAT ) );
	}

	/** Makes shield enchantable. */
	@Override
	public int getEnchantmentValue() {
		return 1;
	}
}