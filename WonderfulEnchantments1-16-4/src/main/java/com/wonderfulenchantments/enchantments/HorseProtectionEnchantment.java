package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.attributes.AttributeHandler;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.ConfigHandlerOld.Config;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that increases horse's armor. */
@Mod.EventBusSubscriber
public class HorseProtectionEnchantment extends Enchantment {
	protected static final AttributeHandler attributeHandler = new AttributeHandler( "f7f6f46b-23a1-4d3b-8e83-3160c6390f9a", "HorseProtectionBonus",
		Attributes.ARMOR, AttributeModifier.Operation.ADDITION
	);

	public HorseProtectionEnchantment() {
		super( Rarity.UNCOMMON, WonderfulEnchantmentHelper.HORSE_ARMOR, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 1 + 6 * ( level - 1 ) + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + level * 10;
	}

	/** Event that updates the armor bonus on each animal entity equipment change. */
	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		if( livingEntity instanceof AnimalEntity )
			attributeHandler.setValue( getArmorBonus( ( AnimalEntity )livingEntity ) )
				.apply( livingEntity );
	}

	/**
	 Calculating the armor bonus of equipped horse armor.

	 @param animal Animal on which the armor bonus is calculated.
	 */
	protected static double getArmorBonus( AnimalEntity animal ) {
		int protectionLevel = EnchantmentHelperPlus.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.HORSE_PROTECTION.get(),
			animal.getArmorInventoryList(), HorseArmorItem.class
		);

		return protectionLevel * Config.HORSE_ARMOR_BONUS.get();
	}
}
