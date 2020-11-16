package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EnchantmentTypes;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "575cb29a-1ee4-11eb-adc1-0242ac120002" );
	protected static final String MODIFIER_NAME = "VitalityBonus";

	public VitalityEnchantment() {
		super( Rarity.RARE, EnchantmentTypes.SHIELD, EquipmentSlotTypes.BOTH_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 5 + 8 * ( level ) + ( ConfigHandler.Values.VITALITY.get() ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 10;
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		LivingEntity entity = event.getEntityLiving();

		ModifiableAttributeInstance maxHealth = entity.getAttribute( Attributes.field_233818_a_ ); // IAttributeInstance -> ModifiableAttributeInstance   ||   field_233818_a_ -> MAX_HEALTH

		maxHealth.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, 2 * getVitalityBonus( entity ), AttributeModifier.Operation.ADDITION );
		maxHealth.func_233767_b_( modifier ); // func_233767_b_ -> applyModifier
	}

	private static int getVitalityBonus( LivingEntity entity ) {
		int sum = 0;

		ItemStack item1 = entity.getHeldItemMainhand(), item2 = entity.getHeldItemOffhand();

		if( item1.getItem() instanceof ShieldItem )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY.get(), item1 );

		if( item2.getItem() instanceof ShieldItem )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY.get(), item2 );

		return sum;
	}
}