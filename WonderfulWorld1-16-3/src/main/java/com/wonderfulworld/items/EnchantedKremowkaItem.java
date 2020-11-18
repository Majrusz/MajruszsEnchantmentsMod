package com.wonderfulworld.items;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class EnchantedKremowkaItem extends Item {
	public EnchantedKremowkaItem() {
		super( new Properties()
			.rarity( Rarity.EPIC )
			.group( WonderfulWorld.TAB_ITEMS )
			.food( new Food.Builder()
				.hunger( 12 )
				.saturation( 1.5f )
				.effect( () -> new EffectInstance( Effects.LUCK, 6000, 4 ), 1.00f )
				.effect( () -> new EffectInstance( Effects.REGENERATION, 3000, 0 ), 1.00f )
				.setAlwaysEdible()
				.build()
			)
		);
	}

	@Override
	public boolean hasEffect( ItemStack stack ) {
		return true;
	}
}
