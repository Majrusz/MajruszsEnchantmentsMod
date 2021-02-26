package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;

/** Makes all other enchantments incompatible with this one. */
public class IncompatibilityCurse extends WonderfulCurse {
	public IncompatibilityCurse() {
		super( "incompatibility_curse", Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS, "Incompatibility" );
		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return false;
	}
}
