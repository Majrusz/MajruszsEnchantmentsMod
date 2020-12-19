package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.*;
import com.wonderfulenchantments.ConfigHandler.Config;
import com.wonderfulenchantments.AttributeHelper.Attributes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

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

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		if( livingEntity instanceof HorseEntity )
			attributeHelper.setValue( getMovementSpeedMultiplier( ( HorseEntity )livingEntity ) )
				.apply( livingEntity );
	}

	protected static double getMovementSpeedMultiplier( HorseEntity horse ) {
		int swiftnessLevel = WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.SWIFTNESS.get(), horse.getArmorInventoryList() );

		return swiftnessLevel * Config.SWIFTNESS_MULTIPLIER.get();
	}
}
