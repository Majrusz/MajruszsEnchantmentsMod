package com.wonderfulenchantments.curses;

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
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class SlownessCurse extends Enchantment {
	protected static final UUID MODIFIER_UUID = UUID.fromString( "760f7b82-76c7-4875-821e-ef0579b881e0" );
	protected static final String MODIFIER_NAME = "SlownessCurse";

	public SlownessCurse( String name ) {
		super( Rarity.UNCOMMON, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlots.ARMOR_AND_HANDS );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 25 + ( ConfigHandler.Curses.SLOWNESS ? 0 : RegistryHandler.disableEnchantmentValue );
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
		EntityLivingBase entity = event.getEntityLiving();

		IAttributeInstance movementSpeed = entity.getEntityAttribute( SharedMonsterAttributes.MOVEMENT_SPEED );

		movementSpeed.removeModifier( MODIFIER_UUID );
		AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, getSlownessBonus( entity ), Constants.AttributeModifierOperation.MULTIPLY );
		movementSpeed.applyModifier( modifier );
	}

	private static double getSlownessBonus( EntityLivingBase entity ) {
		int sum = 0;

		ItemStack item1 = entity.getHeldItemMainhand(), item2 = entity.getHeldItemOffhand();

		if( item1.getItem() instanceof ItemShield )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS, item1 );

		if( item2.getItem() instanceof ItemShield )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS, item2 );

		for( ItemStack stack : entity.getArmorInventoryList() )
			sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SLOWNESS, stack );

		return -( ( double )( sum ) * 0.125D );
	}
}
