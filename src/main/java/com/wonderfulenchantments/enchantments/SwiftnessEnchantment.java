package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.HorseArmorItem;
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
		super( "swiftness", Rarity.RARE, RegistryHandler.HORSE_ARMOR, EquipmentSlots.ARMOR, "Swiftness" );
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
		LivingEntity entity = event.getEntityLiving();

		if( entity instanceof Animal )
			ATTRIBUTE_HANDLER.setValueAndApply( entity, getMovementSpeedMultiplier( ( Animal )entity ) );
	}

	/**
	 Calculating the movement speed bonuses of equipped horse armor.

	 @param animal Animal on which the movement bonus is calculated.
	 */
	protected static double getMovementSpeedMultiplier( Animal animal ) {
		int swiftnessLevel = EnchantmentHelperPlus.calculateEnchantmentSumIfIsInstanceOf( Instances.SWIFTNESS, animal.getArmorSlots(),
			HorseArmorItem.class
		);

		return swiftnessLevel * Instances.SWIFTNESS.movementMultiplier.get();
	}
}
