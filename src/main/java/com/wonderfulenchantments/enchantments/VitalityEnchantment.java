package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases the health of the entity. */
@Mod.EventBusSubscriber
public class VitalityEnchantment extends WonderfulEnchantment {
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "575cb29a-1ee4-11eb-adc1-0242ac120002", "VitalityBonus",
		Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION
	);
	protected final DoubleConfig healthBonus;

	public VitalityEnchantment() {
		super( Rarity.UNCOMMON, RegistryHandler.SHIELD, EquipmentSlotTypes.BOTH_HANDS, "Vitality" );
		String comment = "Health bonus per enchantment level.";
		this.healthBonus = new DoubleConfig( "health_bonus", comment, false, 2.0, 1.0, 20.0 );
		this.enchantmentGroup.addConfig( this.healthBonus );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 10 );
		setMinimumEnchantabilityCalculator( level->( 5 + 8 * level ) );
	}

	/** Event that updates the health bonus on each living entity equipment change. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		ATTRIBUTE_HANDLER.setValueAndApply( entity, getHealthBonus( entity ) );
	}

	/**
	 Calculating the sum of health bonuses on both shields.

	 @param livingEntity Entity on which the health bonus is calculated.
	 */
	protected static double getHealthBonus( LivingEntity livingEntity ) {
		int sum = EnchantmentHelperPlus.calculateEnchantmentSumIfIsInstanceOf( Instances.VITALITY, livingEntity, EquipmentSlotTypes.BOTH_HANDS,
			ShieldItem.class
		);

		return sum * Instances.VITALITY.healthBonus.get();
	}
}
