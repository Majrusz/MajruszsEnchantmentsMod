package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases the movement speed of the horse. */
@Mod.EventBusSubscriber
public class SwiftnessEnchantment extends WonderfulEnchantment {
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "76c3bea2-7ef1-4c4b-b062-a12355120ee7", "SwiftnessBonus",
		Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE
	);
	protected final DoubleConfig movementMultiplier;

	public SwiftnessEnchantment() {
		super( Rarity.RARE, RegistryHandler.HORSE_ARMOR, EquipmentSlotTypes.ARMOR, "Swiftness" );
		String comment = "Horse movement speed multiplier per enchantment level.";
		this.movementMultiplier = new DoubleConfig( "movement_multiplier", comment, false, 0.125, 0.01, 0.5 );
		this.enchantmentGroup.addConfig( this.movementMultiplier );

		setMaximumEnchantmentLevel( 4 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 5 * level ) );
	}

	/** Event that updates the movement speed bonus on each animal entity equipment change. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		if( livingEntity instanceof AnimalEntity )
			ATTRIBUTE_HANDLER.setValue( getMovementSpeedMultiplier( ( AnimalEntity )livingEntity ) )
				.apply( livingEntity );
	}

	/**
	 Calculating the movement speed bonuses of equipped horse armor.

	 @param animal Animal on which the movement bonus is calculated.
	 */
	protected static double getMovementSpeedMultiplier( AnimalEntity animal ) {
		int swiftnessLevel = EnchantmentHelperPlus.calculateEnchantmentSumIfIsInstanceOf( Instances.SWIFTNESS, animal.getArmorInventoryList(),
			HorseArmorItem.class
		);

		return swiftnessLevel * Instances.SWIFTNESS.movementMultiplier.get();
	}
}
