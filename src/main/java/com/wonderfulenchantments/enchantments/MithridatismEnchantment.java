package com.wonderfulenchantments.enchantments;

import com.mlib.config.*;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that gives absorption and mithridatism immunity after any negative effect is applied to the player. */
@Mod.EventBusSubscriber
public class MithridatismEnchantment extends WonderfulEnchantment {
	public MithridatismEnchantment() {
		super( "mithridatism", Rarity.VERY_RARE, EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST, "Mithridatism" );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 + 100 * ( level - 1 ) ) );
	}

	/** Effect that decreases damage from certain negative effects. */
	public static class MithridatismProtectionEffect extends Effect {
		protected final ConfigGroup effectGroup;
		protected final StringListConfig damageSourceList;
		protected final IntegerConfig absorptionPerLevel;
		protected final DoubleConfig baseDamageReduction, damageReductionPerLevel;
		protected final DurationConfig duration;

		protected MithridatismProtectionEffect() {
			super( EffectType.BENEFICIAL, 0xff76db4c );

			String list_comment = "";
			String absorption_comment = "Level of Absorption applied to the player per enchantment level.";
			String base_reduction_comment = "Base amount of damage decreased from negative effects.";
			String level_reduction_comment = "Amount of damage decreased from negative effects per enchantment level.";
			String duration_comment = "Duration of both the Absorption and Mithridatism Protection. (in seconds)";
			this.effectGroup = new ConfigGroup( "MithridatismProtection", "" );
			this.damageSourceList = new StringListConfig( "damage_source_list", list_comment, false, "poison", "wither", "bleeding" );
			this.absorptionPerLevel = new IntegerConfig( "absorption_per_level", absorption_comment, false, 1, 0, 3 );
			this.baseDamageReduction = new DoubleConfig( "base_reduction", base_reduction_comment, false, 0.15, 0.0, 1.0 );
			this.damageReductionPerLevel = new DoubleConfig( "reduction_per_level", level_reduction_comment, false, 0.15, 0.0, 1.0 );
			this.duration = new DurationConfig( "duration", duration_comment, false, 30.0, 2.0, 600.0 );
		}
	}

}
