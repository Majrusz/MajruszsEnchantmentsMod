package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;
import static com.wonderfulenchantments.WonderfulEnchantmentHelper.ticksInMinute;

@Mod.EventBusSubscriber
public class GottaMineFastEnchantment extends Enchantment {
	static private int tickCounter = 0;
	static private boolean isMining = false;

	public GottaMineFastEnchantment() {
		super( Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 15 * level + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 25;
	}

	@SubscribeEvent
	public static void whenHoldingMouseButton( InputEvent.MouseInputEvent event ) {
		if( event.getButton() == 0 )
			isMining = event.getAction() == 1; // when pressed left mouse button
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		tickCounter = isMining ? tickCounter + 1 : 0;
	}

	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.GOTTA_MINE_FAST.get(), event.getPlayer() );

		if( enchantmentLevel > 0 )
			event.setNewSpeed( event.getNewSpeed() * getMiningMultiplier() );
	}

	protected static float getMiningMultiplier() {
		return 1.0f + ( float )Math.pow( Math.min( tickCounter, 2 * ticksInMinute ) / ( float )ticksInMinute, 1.5849625007f );
	}
}
