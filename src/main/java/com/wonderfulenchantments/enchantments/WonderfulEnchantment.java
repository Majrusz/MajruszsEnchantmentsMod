package com.wonderfulenchantments.enchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.ExtendedEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.ArrayList;
import java.util.List;

import static com.wonderfulenchantments.WonderfulEnchantments.ENCHANTMENT_GROUP;

/** Enchantment that automatically adds base config to it. */
public class WonderfulEnchantment extends ExtendedEnchantment {
	public static final List< WonderfulEnchantment > ENCHANTMENT_LIST = new ArrayList<>();
	protected final ConfigGroup enchantmentGroup;
	protected final AvailabilityConfig availabilityConfig;

	protected WonderfulEnchantment( String registerName, Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType[] equipmentSlotTypes,
		String configName
	) {
		super( registerName, rarity, enchantmentType, equipmentSlotTypes );

		this.enchantmentGroup = ENCHANTMENT_GROUP.addGroup( new ConfigGroup( configName, "" ) );

		String comment = "Makes this enchantment obtainable in survival mode.";
		this.availabilityConfig = this.enchantmentGroup.addConfig( new AvailabilityConfig( "is_enabled", comment, false, true ) );

		ENCHANTMENT_LIST.add( this );
	}

	protected WonderfulEnchantment( String registerName, Rarity rarity, EnchantmentType enchantmentType, EquipmentSlotType equipmentSlotType,
		String configName
	) {
		this( registerName, rarity, enchantmentType, new EquipmentSlotType[]{ equipmentSlotType }, configName );
	}

	@Override
	protected boolean isDisabled() {
		return this.availabilityConfig.isDisabled();
	}
}
