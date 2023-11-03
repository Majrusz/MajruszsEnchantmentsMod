package com.majruszsenchantments.curses;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EquipmentSlots;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class IncompatibilityCurse extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.compatibility( enchantment->false );
	}

	public IncompatibilityCurse() {
		super( MajruszsEnchantments.INCOMPATIBILITY, true );
	}
}
