package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Causes entity to move slower with each level. */
@Mod.EventBusSubscriber
public class SlownessCurse extends WonderfulCurse {
	protected static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "760f7b82-76c7-4875-821e-ef0579b881e0", "SlownessCurse",
		Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL
	);
	protected final DoubleConfig slownessMultiplierConfig;

	public SlownessCurse() {
		super( Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS, "Slowness" );
		String comment = "Cumulative movement speed reduction with each item with this curse.";
		this.slownessMultiplierConfig = new DoubleConfig( "multiplier", comment, false, 0.875, 0.1, 0.95 );
		this.curseGroup.addConfig( this.slownessMultiplierConfig );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	/** Called when entity is changing equipment. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		ATTRIBUTE_HANDLER.setValue( Instances.SLOWNESS.getTotalSlownessMultiplier( entity ) - 1.0 )
			.apply( entity );
	}

	/** Calculates total slowness multiplier. (sum of slowness enchantment level on every armor piece) */
	private double getTotalSlownessMultiplier( LivingEntity entity ) {
		int sum = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.SLOWNESS, entity, EquipmentSlotTypes.ARMOR );

		return Math.pow( this.slownessMultiplierConfig.get(), sum );
	}
}
