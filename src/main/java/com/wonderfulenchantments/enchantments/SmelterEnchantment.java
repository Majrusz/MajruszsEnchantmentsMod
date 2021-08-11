package com.wonderfulenchantments.enchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.StringListConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

/** Enchantment that automatically smelts destroyed blocks. (if possible) */
public class SmelterEnchantment extends WonderfulEnchantment {
	protected final AvailabilityConfig shouldIncreaseLoot;
	protected final StringListConfig fortuneBonusList;

	public SmelterEnchantment() {
		super( "smelter", Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, "Smelter" );

		String lootComment = "Should this enchantment duplicate custom items when player have fortune enchantment.";
		this.shouldIncreaseLoot = new AvailabilityConfig( "should_increase_loot", lootComment, false, true );

		String bonusComment = "List of blocks that will drop more items when player has both Fortune and Smelter enchantment.";
		this.fortuneBonusList = new StringListConfig( "fortune_bonus_list", bonusComment, false, "for example: minecraft:iron_ore" );

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
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof UntouchingEnchantment ) && super.checkCompatibility( enchantment );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack ) {
		return !( stack.getItem() instanceof HoeItem ) && super.canApplyAtEnchantingTable( stack );
	}
}
