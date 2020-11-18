package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class BassItem extends Item {
	public BassItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 2 )
				.saturation( 0.4f )
				.build()
			)
		);
	}	
}
