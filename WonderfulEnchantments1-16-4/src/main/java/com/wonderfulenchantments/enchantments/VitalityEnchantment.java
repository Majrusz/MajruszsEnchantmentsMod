package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.AttributeHelper;
import com.wonderfulenchantments.AttributeHelper.Attributes;
import com.wonderfulenchantments.ConfigHandler.Config;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that increases the health of the entity. */
@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "575cb29a-1ee4-11eb-adc1-0242ac120002", "VitalityBonus",
		Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION
	);

	public VitalityEnchantment() {
		super( Rarity.UNCOMMON, WonderfulEnchantmentHelper.SHIELD, EquipmentSlotTypes.BOTH_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 5 + 8 * level + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 10;
	}

	/** Event that updates the health bonus on each living entity equipment change. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		attributeHelper.setValue( getHealthBonus( livingEntity ) )
			.apply( livingEntity );
	}

	/** Calculating the sum of health bonuses on both shields.
	 * @param livingEntity Entity on which the health bonus is calculated.
	 */
	protected static double getHealthBonus( LivingEntity livingEntity ) {
		int sum = WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.VITALITY.get(), livingEntity,
			EquipmentSlotTypes.BOTH_HANDS, ShieldItem.class
		);

		return sum * Config.VITALITY_BONUS.get();
	}
}
