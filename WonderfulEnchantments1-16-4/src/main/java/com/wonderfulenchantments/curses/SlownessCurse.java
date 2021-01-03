package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.*;
import com.wonderfulenchantments.ConfigHandler.Config;
import com.wonderfulenchantments.AttributeHelper.Attributes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class SlownessCurse extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "760f7b82-76c7-4875-821e-ef0579b881e0", "SlownessCurse",
		Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL
	);

	public SlownessCurse() {
		super( Rarity.RARE, EnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 40;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return true;
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		attributeHelper.setValue( getTotalSlownessMultiplier( livingEntity ) )
			.apply( livingEntity );
	}

	private static float getTotalSlownessMultiplier( LivingEntity livingEntity ) {
		int sum = 0;

		sum += WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.SLOWNESS.get(), livingEntity, EquipmentSlotTypes.ARMOR );
		sum += WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.SLOWNESS.get(), livingEntity,
			EquipmentSlotTypes.BOTH_HANDS, ShieldItem.class
		);

		return ( float )-( sum * Config.SLOWNESS_MULTIPLIER.get() );
	}
}
