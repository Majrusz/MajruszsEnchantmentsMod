package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class IncompatibilityCurse extends CustomEnchantment {
	public IncompatibilityCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return false;
	}

	@AutoInstance
	public static class Handler {
		final Supplier< IncompatibilityCurse > enchantment = Registries.INCOMPATIBILITY;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Incompatibility" )
				.comment( "Makes all other enchantments incompatible with this one." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );
		}
	}
}
