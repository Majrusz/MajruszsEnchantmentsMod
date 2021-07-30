package com.wonderfulenchantments.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

/** Makes leather horse armor enchantable. */
public class DyeableHorseArmorItemReplacement extends DyeableHorseArmorItem implements DyeableLeatherItem {
	public DyeableHorseArmorItemReplacement( int armorBonus, String resource ) {
		super( armorBonus, resource, ( new Properties() ).stacksTo( 1 )
			.tab( CreativeModeTab.TAB_MISC ) );
	}

	public DyeableHorseArmorItemReplacement( int armorBonus, ResourceLocation texture ) {
		super( armorBonus, texture, ( new Properties() ).stacksTo( 1 )
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
