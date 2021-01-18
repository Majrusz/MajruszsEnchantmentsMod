package com.wonderfulenchantments.enchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.ExtendedEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import static com.wonderfulenchantments.WonderfulEnchantments.ENCHANTMENT_GROUP;

/** Enchantment that automatically adds config to it. */
public class WonderfulEnchantment extends ExtendedEnchantment {
	protected final ConfigGroup enchantmentGroup;
	protected final AvailabilityConfig availabilityConfig;

	protected WonderfulEnchantment( Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] equipmentSlotTypes, String configName ) {
		super( rarity, enchantmentType, equipmentSlotTypes );
		String comment = "Makes this enchantment obtainable in survival mode.";

		this.enchantmentGroup = ENCHANTMENT_GROUP.addGroup( new ConfigGroup( configName, "" ) );
		this.availabilityConfig = this.enchantmentGroup.addConfig( new AvailabilityConfig( "is_enabled", comment, false, true ) );
	}

	protected WonderfulEnchantment( Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType equipmentSlotType, String configName ) {
		this( rarity, enchantmentType, new EquipmentSlotType[]{ equipmentSlotType }, configName );
	}

	@Override
	protected boolean isDisabled() {
		return this.availabilityConfig.isDisabled();
	}
}
