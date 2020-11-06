package com.wonderfulenchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;

public class EnchantmentTypes {
	public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item item )->item instanceof ShieldItem );

	public static void addTypeToItemGroup( EnchantmentType type, ItemGroup itemGroup ) {
		EnchantmentType[] group = itemGroup.getRelevantEnchantmentTypes();
		EnchantmentType[] temporary = new EnchantmentType[ group.length + 1 ];
		System.arraycopy( group, 0, temporary, 0, group.length );
		temporary[ group.length - 1 ] = type;
		itemGroup.setRelevantEnchantmentTypes( temporary );
	}
}
