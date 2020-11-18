package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class FatigueCurse extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "c0c5f9c4-3b34-4cd6-aa13-6340d9d21f11" );
	protected static final String MODIFIER_NAME = "FatigueCurse";

	public FatigueCurse() {
		super( Rarity.UNCOMMON, EnchantmentType.DIGGER, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + ( ConfigHandler.Values.FATIGUE.get() ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 25;
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
		LivingEntity entity = event.getEntityLiving();

		ModifiableAttributeInstance movementSpeed = entity.getAttribute( Attributes.field_233821_d_ );

		movementSpeed.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, getSlownessBonus( entity ), AttributeModifier.Operation.MULTIPLY_TOTAL );
		movementSpeed.func_233767_b_( modifier );
	}

	private static double getSlownessBonus( LivingEntity entity ) {
		int sum = 0;

		ItemStack item1 = entity.getHeldItemMainhand(), item2 = entity.getHeldItemOffhand();

		if( item1.getItem() instanceof ShieldItem )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS.get(), item1 );

		if( item2.getItem() instanceof ShieldItem )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS.get(), item2 );

		for( ItemStack stack : entity.getArmorInventoryList() )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS.get(), stack );

		return -( ( double )( sum ) * 0.125D );
	}
}
