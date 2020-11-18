package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class SurstrommingItem extends Item {
	public SurstrommingItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 12 )
				.saturation( 1.0f )
				.effect( () -> new EffectInstance( Effects.NAUSEA, 400, 0 ), 0.20f )
				.effect( () -> new EffectInstance( Effects.REGENERATION, 400, 0 ), 0.50f )
				.effect( () -> new EffectInstance( Effects.ABSORPTION, 2400, 0 ), 0.50f )
				.build()
			)
		);
	}	
}
