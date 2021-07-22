package com.wonderfulenchantments.curses;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.ExtendedCurse;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.ArrayList;
import java.util.List;

import static com.wonderfulenchantments.WonderfulEnchantments.CURSE_GROUP;

/** Curse that automatically adds config to it. */
public class WonderfulCurse extends ExtendedCurse {
	public static final List< WonderfulCurse > CURSE_LIST = new ArrayList<>();
	protected final ConfigGroup curseGroup;
	protected final AvailabilityConfig availabilityConfig;

	protected WonderfulCurse( String registerName, Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] equipmentSlotTypes,
		String configName
	) {
		super( registerName, rarity, enchantmentType, equipmentSlotTypes );

		this.curseGroup = CURSE_GROUP.addGroup( new ConfigGroup( configName, "" ) );

		String comment = "Makes this curse obtainable in survival mode.";
		this.availabilityConfig = this.curseGroup.addConfig( new AvailabilityConfig( "is_enabled", comment, false, true ) );

		CURSE_LIST.add( this );
	}

	protected WonderfulCurse( String registerName, Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType equipmentSlotType,
		String configName
	) {
		this( registerName, rarity, enchantmentType, new EquipmentSlotType[]{ equipmentSlotType }, configName );
	}

	@Override
	protected boolean isDisabled() {
		return this.availabilityConfig.isDisabled();
	}
}
