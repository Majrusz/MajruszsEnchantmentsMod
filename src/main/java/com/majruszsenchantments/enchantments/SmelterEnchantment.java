package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnBlockSmeltCheck;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;

public class SmelterEnchantment extends CustomEnchantment {
	public SmelterEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( EnchantmentCategory.DIGGER )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof UntouchingEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< SmelterEnchantment > {
		public Modifier() {
			super( Registries.SMELTER, Registries.Modifiers.ENCHANTMENT );

			new OnBlockSmeltCheck.Context( OnBlockSmeltCheck.ENABLE_SMELT )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.insertTo( this );

			this.name( "Smelter" ).comment( "Destroyed blocks are automatically smelted." );
		}
	}
}
