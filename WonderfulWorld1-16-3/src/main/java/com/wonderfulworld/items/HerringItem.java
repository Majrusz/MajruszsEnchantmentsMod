package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class HerringItem extends Item {
	public HerringItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 3 )
				.saturation( 0.6f )
				.build()
			)
		);
	}	
}
