package com.majruszsenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class IncompatibilityCurse extends CustomEnchantment {
	public static Supplier< IncompatibilityCurse > create() {
		CustomEnchantment.Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlots.ARMOR_AND_HANDS, true, 1, level->10, level->50 );
		IncompatibilityCurse enchantment = new IncompatibilityCurse( params );
		Modifier modifier = new IncompatibilityCurse.Modifier( enchantment );

		return ()->enchantment;
	}

	public IncompatibilityCurse( Parameters params ) {
		super( params );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return false;
	}

	private static class Modifier extends EnchantmentModifier< IncompatibilityCurse > {
		public Modifier( IncompatibilityCurse enchantment ) {
			super( enchantment, "Incompatibility", "Makes all other enchantments incompatible with this one." );
		}
	}
}
