package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
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
		super( "slowness_curse", Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "Slowness" );
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

		ATTRIBUTE_HANDLER.setValueAndApply( entity, Instances.MOVEMENT_SLOWDOWN.getTotalSlownessMultiplier( entity ) - 1.0 );
	}

	/** Calculates total slowness multiplier. (sum of slowness enchantment level on every armor piece) */
	private double getTotalSlownessMultiplier( LivingEntity entity ) {
		int sum = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.MOVEMENT_SLOWDOWN, entity, EquipmentSlots.ARMOR );

		return Math.pow( this.slownessMultiplierConfig.get(), sum );
	}
}
