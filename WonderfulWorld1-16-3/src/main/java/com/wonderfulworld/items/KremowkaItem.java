package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class KremowkaItem extends Item {
	public KremowkaItem() {
		super( new Properties()
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 6 )
				.saturation( 1.34f )
				.effect( () -> new EffectInstance( Effects.LUCK, 2400, 0 ), 0.20f )
				.effect( () -> new EffectInstance( Effects.LUCK, 2400, 1 ), 0.10f )
				.effect( () -> new EffectInstance( Effects.LUCK, 2400, 2 ), 0.05f )
				.setAlwaysEdible()
				.build()
			)
		);

	}
}
