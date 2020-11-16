package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class SlownessCurse extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "760f7b82-76c7-4875-821e-ef0579b881e0" );
	protected static final String MODIFIER_NAME = "SlownessCurse";

	public SlownessCurse() {
		super( Rarity.UNCOMMON, EnchantmentType.BREAKABLE, EquipmentSlotTypes.ARMOR_AND_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 25 + ( ConfigHandler.Values.SLOWNESS.get() ? 0 : RegistryHandler.disableEnchantmentValue );
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

		IAttributeInstance movementSpeed = entity.getAttribute( SharedMonsterAttributes.MOVEMENT_SPEED );

		movementSpeed.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, getSlownessBonus( entity ), AttributeModifier.Operation.MULTIPLY_TOTAL );
		movementSpeed.applyModifier( modifier );
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
