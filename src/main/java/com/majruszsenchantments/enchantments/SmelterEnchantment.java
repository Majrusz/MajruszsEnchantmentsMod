package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnBlockSmeltCheck;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;

import java.util.function.Supplier;

public class SmelterEnchantment extends CustomEnchantment {
	public SmelterEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( EnchantmentCategory.DIGGER )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof UntouchingEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Handler {
		final Supplier< SmelterEnchantment > enchantment = Registries.SMELTER;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Smelter" )
				.comment( "Destroyed blocks are automatically smelted." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnBlockSmeltCheck.listen( OnBlockSmeltCheck.ENABLE_SMELT )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.insertTo( group );
		}
	}
}
