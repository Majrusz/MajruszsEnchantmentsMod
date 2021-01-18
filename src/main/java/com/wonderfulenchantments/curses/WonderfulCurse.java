package com.wonderfulenchantments.curses;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.ExtendedCurse;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import static com.wonderfulenchantments.WonderfulEnchantments.CURSE_GROUP;

/** Curse that automatically adds config to it. */
public class WonderfulCurse extends ExtendedCurse {
	protected final ConfigGroup curseGroup;
	protected final AvailabilityConfig availabilityConfig;

	protected WonderfulCurse( Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] equipmentSlotTypes, String configName ) {
		super( rarity, enchantmentType, equipmentSlotTypes );
		String comment = "Makes this curse obtainable in survival mode.";

		this.curseGroup = CURSE_GROUP.addGroup( new ConfigGroup( configName, "" ) );
		this.availabilityConfig = this.curseGroup.addConfig( new AvailabilityConfig( "is_enabled", comment, false, true ) );
	}

	protected WonderfulCurse( Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType equipmentSlotType, String configName ) {
		this( rarity, enchantmentType, new EquipmentSlotType[]{ equipmentSlotType }, configName );
	}

	@Override
	protected boolean isDisabled() {
		return this.availabilityConfig.isDisabled();
	}
}
