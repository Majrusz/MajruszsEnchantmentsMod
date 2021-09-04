package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.EquipmentSlots;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
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
		Animal animal = CommonHelper.castIfPossible( Animal.class, event.getEntityLiving() );
		if( animal != null ) // it checks for the animal class instead of horse only to have a compatibility with other mods
			ATTRIBUTE_HANDLER.setValueAndApply( animal, Instances.SWIFTNESS.getMovementSpeedMultiplier( animal ) );
	}

	/** Calculates the movement speed bonus of a horse armor. */
	protected double getMovementSpeedMultiplier( Animal animal ) {
		return getEnchantmentSum( animal.getArmorSlots() ) * this.movementMultiplier.get();
	}
}
