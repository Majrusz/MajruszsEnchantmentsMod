package com.majruszsenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnBlockSmeltCheckContext;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;

import java.util.function.Supplier;

public class SmelterEnchantment extends CustomEnchantment {
	public static Supplier< SmelterEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlots.MAINHAND, false, 1, level->15, level->45 );
		SmelterEnchantment enchantment = new SmelterEnchantment( params );
		Modifier modifier = new SmelterEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public SmelterEnchantment( Parameters params ) {
		super( params );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof UntouchingEnchantment ) && super.checkCompatibility( enchantment );
	}

	private static class Modifier extends EnchantmentModifier< SmelterEnchantment > {
		public Modifier( SmelterEnchantment enchantment ) {
			super( enchantment, "Smelter", "Destroyed blocks are automatically smelted." );

			OnBlockSmeltCheckContext onCheck = new OnBlockSmeltCheckContext( OnBlockSmeltCheckContext.ENABLE_SMELT );
			onCheck.addCondition( new Condition.HasEnchantment( enchantment ) );

			this.addContexts( onCheck );
		}
	}
}
