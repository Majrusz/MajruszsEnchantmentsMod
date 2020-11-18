package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CakeSliceItem extends Item {
	public CakeSliceItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 4 )
				.saturation( 0.2f )
				.fastToEat()
				.build()
			)
		);
	}
}