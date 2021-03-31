package com.wonderfulenchantments.enchantments;

import com.mlib.config.StringListConfig;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that gives absorption and mithridatism immunity after any negative effect is applied to the player. */
@Mod.EventBusSubscriber
public class MithridatismEnchantment extends WonderfulEnchantment {
	protected final StringListConfig damageSourceList;

	public MithridatismEnchantment() {
		super( "mithridatism", Rarity.VERY_RARE, EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST, "Mithridatism" );

		String list_comment = "";
		this.damageSourceList = new StringListConfig( "damage_source_list", list_comment, false, "poison", "wither" );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 + 100 * ( level - 1 ) ) );
	}
}
