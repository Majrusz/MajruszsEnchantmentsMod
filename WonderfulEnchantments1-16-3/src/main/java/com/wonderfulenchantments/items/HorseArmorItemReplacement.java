package com.wonderfulenchantments.items;

import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class HorseArmorItemReplacement extends HorseArmorItem {
	public HorseArmorItemReplacement( int armorBonus, String resource ) {
		super( armorBonus, resource, ( new Properties() ).maxStackSize( 1 )
			.group( ItemGroup.MISC ) );
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}

	@Override
	public boolean isEnchantable( ItemStack stack ) {
		return this.getItemStackLimit( stack ) == 1;
	}
}
