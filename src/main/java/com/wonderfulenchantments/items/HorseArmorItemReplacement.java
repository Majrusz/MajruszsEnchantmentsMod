package com.wonderfulenchantments.items;

import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Makes horse armor enchantable. */
public class HorseArmorItemReplacement extends HorseArmorItem {
	public HorseArmorItemReplacement( int armorBonus, String resource ) {
		super( armorBonus, resource, ( new Properties() ).stacksTo( 1 )
			.tab( CreativeModeTab.TAB_MISC ) );
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public boolean isEnchantable( ItemStack stack ) {
		return this.getItemStackLimit( stack ) == 1;
	}
}
