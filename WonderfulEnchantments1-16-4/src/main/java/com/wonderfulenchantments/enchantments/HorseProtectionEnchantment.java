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

@Mod.EventBusSubscriber
public class HorseProtectionEnchantment extends Enchantment {
	protected static final AttributeHelper attributeHelper = new AttributeHelper( "f7f6f46b-23a1-4d3b-8e83-3160c6390f9a", "HorseProtectionBonus",
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

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();

		if( livingEntity instanceof AnimalEntity )
			attributeHelper.setValue( getArmorBonus( ( AnimalEntity )livingEntity ) )
				.apply( livingEntity );
	}

	protected static double getArmorBonus( AnimalEntity animal ) {
		int protectionLevel = WonderfulEnchantmentHelper.calculateEnchantmentSumIfIsInstanceOf( RegistryHandler.HORSE_PROTECTION.get(),
			animal.getArmorInventoryList(), HorseArmorItem.class
		);

		return protectionLevel * Config.HORSE_ARMOR_BONUS.get();
	}
}
