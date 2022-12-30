package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class IncompatibilityCurse extends CustomEnchantment {
	public IncompatibilityCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return false;
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< IncompatibilityCurse > {
		public Modifier() {
			super( Registries.INCOMPATIBILITY, Registries.Modifiers.CURSE );

			this.name( "Incompatibility" ).comment( "Makes all other enchantments incompatible with this one." );
		}
	}
}
