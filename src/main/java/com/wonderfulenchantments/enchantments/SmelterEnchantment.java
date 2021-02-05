package com.wonderfulenchantments.enchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.DoubleConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;

/** Enchantment that automatically smelts destroyed blocks. (if possible) */
public class SmelterEnchantment extends WonderfulEnchantment {
	protected final AvailabilityConfig shouldIncreaseLoot;

	public SmelterEnchantment() {
		super( Rarity.UNCOMMON, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "Smelter" );
		String comment = "Should this enchantment duplicate items like iron ore or gold ore when player have fortune enchantment.                                                                                                                                                                                         ";
		this.shouldIncreaseLoot = new AvailabilityConfig( "should_increase_loot", comment, false, false );
		this.enchantmentGroup.addConfig( this.shouldIncreaseLoot );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}

	/** Checks whether Smelter enchantment should duplicate items like iron ore or gold ore. */
	public boolean isExtraLootDisabled() {
		return this.shouldIncreaseLoot.isDisabled();
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof SilkTouchEnchantment ) && super.canApplyTogether( enchantment );
	}
}
