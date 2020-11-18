package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CookedBassItem extends Item {
	public CookedBassItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 5 )
				.saturation( 1.2f )
				.build()
			)
		);
	}
}
