package com.wonderfulenchantments.enchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.StringListConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Enchantment that automatically smelts destroyed blocks. (if possible) */
public class SmelterEnchantment extends WonderfulEnchantment {
	protected final AvailabilityConfig shouldIncreaseLoot;
	protected final StringListConfig fortuneBonusList;

	public SmelterEnchantment() {
		super( "smelter", Rarity.UNCOMMON, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "Smelter" );
		String availability_comment = "Should this enchantment duplicate custom items (def. iron ore and gold ore) when player have fortune enchantment.";
		String bonus_comment = "List of blocks that will drop more items when player has both Fortune and Smelter enchantment.";
		this.shouldIncreaseLoot = new AvailabilityConfig( "should_increase_loot", availability_comment, false, true );
		this.fortuneBonusList = new StringListConfig( "fortune_bonus_list", bonus_comment, false, "minecraft:iron_ore", "minecraft:gold_ore" );
		this.enchantmentGroup.addConfigs( this.shouldIncreaseLoot, this.fortuneBonusList );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}

	/** Checks whether Smelter enchantment should duplicate items like iron ore or gold ore. */
	public boolean isExtraLootDisabled() {
		return this.shouldIncreaseLoot.isDisabled();
	}

	/** Checks whether block should be affected by Fortune and Smelter. */
	public boolean shouldIncreaseLoot( ResourceLocation blockLocation ) {
		return blockLocation != null && this.fortuneBonusList.contains( blockLocation.toString() );
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof SilkTouchEnchantment ) && super.canApplyTogether( enchantment );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack ) {
		return !( stack.getItem() instanceof HoeItem ) && super.canApplyAtEnchantingTable( stack );
	}
}
