package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/** Makes all other enchantments incompatible with this one. */
public class IncompatibilityCurse extends WonderfulCurse {
	public IncompatibilityCurse() {
		super( "incompatibility_curse", Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlots.ARMOR_AND_HANDS, "Incompatibility" );
		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return false;
	}
}
