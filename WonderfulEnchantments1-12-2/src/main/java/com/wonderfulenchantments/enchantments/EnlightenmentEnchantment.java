package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends Enchantment {
	public EnlightenmentEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 6 + enchantmentLevel * 12 + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@SubscribeEvent
	public static void onXPPickUp( PlayerPickupXpEvent event ) {
		int enlightenmentSum = WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.ENLIGHTENMENT, event.getEntityPlayer(), EquipmentSlotTypes.ARMOR );

		if( enlightenmentSum > 0 ) {
			double bonusRatio = 0.25D * ( double )enlightenmentSum;
			double randomBonus = bonusRatio * WonderfulEnchantments.RANDOM.nextDouble();
			int bonusExp = ( int )( Math.round( randomBonus * ( double )event.getOrb().getXpValue() ) );

			event.getEntityPlayer().addExperience( bonusExp );
		}
	}
}