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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that increases the movement speed of the horse. */
@Mod.EventBusSubscriber
public class SwiftnessEnchantment extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "76c3bea2-7ef1-4c4b-b062-a12355120ee7", "SwiftnessBonus",
		Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE
	);

	public SwiftnessEnchantment() {
		super( Rarity.RARE, WonderfulEnchantmentHelper.HORSE_ARMOR, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 5 * level + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	/** Event that updates the movement speed bonus on each animal entity equipment change. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		if( livingEntity instanceof AnimalEntity )
			attributeHelper.setValue( getMovementSpeedMultiplier( ( AnimalEntity )livingEntity ) )
				.apply( livingEntity );
	}

	/**
	 Calculating the movement speed bonuses of equipped horse armor.

	 @param animal Animal on which the movement bonus is calculated.
	 */
	protected static double getMovementSpeedMultiplier( AnimalEntity animal ) {
		int swiftnessLevel = WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.SWIFTNESS.get(),
			animal.getArmorInventoryList(), HorseArmorItem.class
		);

		return swiftnessLevel * Config.SWIFTNESS_MULTIPLIER.get();
	}
}
