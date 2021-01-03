package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.*;
import com.wonderfulenchantments.AttributeHelper.Attributes;
import com.wonderfulenchantments.ConfigHandler.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class BreakingCurse extends Enchantment {
	public BreakingCurse() {
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
	public static void onBlockHarvest( PlayerEvent.HarvestCheck event ) {
		WonderfulEnchantments.LOGGER.info( "!!!" );
	}

	@SubscribeEvent
	public static void onAttacking( LivingEntityUseItemEvent.Finish event ) {
		WonderfulEnchantments.LOGGER.info( "start" );
	}

	@SubscribeEvent
	public static void onUsingItem( PlayerInteractEvent.RightClickItem event ) {
		WonderfulEnchantments.LOGGER.info( "right" );
	}
}
