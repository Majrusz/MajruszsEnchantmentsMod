package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends Enchantment {
	public EnlightenmentEnchantment() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 6 + enchantmentLevel * 12 + ( ConfigHandler.Values.ENLIGHTENMENT.get() ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		int levelSum = 0;
		for( ItemStack armor : event.getPlayer().getArmorInventoryList() )
			levelSum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.ENLIGHTENMENT.get(), armor );

		if( levelSum > 0 ) {
			double bonusRatio = 0.25D * ( double )levelSum;
			double randomBonus = bonusRatio * WonderfulEnchantments.RANDOM.nextDouble();
			int bonusExp = ( int )( Math.round( randomBonus * ( double )event.getOrb().getXpValue() ) );

			event.getPlayer().giveExperiencePoints( bonusExp );
		}
	}
}