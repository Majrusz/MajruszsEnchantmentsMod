package com.wonderfulenchantments.items;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;

/** Shield item replacement with visible enchantability. */
public class ShieldItemReplacement extends ShieldItem {
	public ShieldItemReplacement() {
		super( ( new Properties() ).maxDamage( 336 )
			.group( ItemGroup.COMBAT ) );
	}

	/** Makes shield enchantable. */
	@Override
	public int getItemEnchantability() {
		return 1;
	}
}