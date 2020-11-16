package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EntityEquipmentSlots;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "575cb29a-1ee4-11eb-adc1-0242ac120002" );
	protected static final String MODIFIER_NAME = "VitalityBonus";

	public VitalityEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlots.BOTH_HANDS );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 5 + 8 * ( level ) + ( ConfigHandler.Enchantments.VITALITY ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 10;
	}

	@Override
	protected boolean canApplyTogether( Enchantment enchant ) {
		return super.canApplyTogether( enchant );
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return super.canApply( stack ) && ( stack.getItem() instanceof ItemShield );
	}

	@SubscribeEvent
	public static void onEquipmentChange( LivingEquipmentChangeEvent event ) {
		EntityLivingBase entity = event.getEntityLiving();

		IAttributeInstance maxHealth = entity.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );

		maxHealth.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, 2 * getVitalityBonus( entity ), Constants.AttributeModifierOperation.ADD );
		maxHealth.applyModifier( modifier );
	}

	private static int getVitalityBonus( EntityLivingBase entity ) {
		int sum = 0;

		ItemStack item1 = entity.getHeldItemMainhand(), item2 = entity.getHeldItemOffhand();

		if( item1.getItem() instanceof ItemShield )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY, item1 );

		if( item2.getItem() instanceof ItemShield )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY, item2 );

		return sum;
	}
}
