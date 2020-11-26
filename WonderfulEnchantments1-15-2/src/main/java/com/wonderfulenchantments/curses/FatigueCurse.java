package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class FatigueCurse extends Enchantment {
	public FatigueCurse() {
		super( Rarity.RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 3;
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
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof EfficiencyEnchantment ) && super.canApplyTogether( enchantment );
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
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		int fatigueLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FATIGUE.get(), event.getPlayer() );

		if( fatigueLevel > 0 )
			event.setNewSpeed( event.getNewSpeed() * getMiningMultiplier( fatigueLevel ) );
	}

	protected static float getMiningMultiplier( int fatigueLevel ) {
		switch( fatigueLevel ) {
			case 1:
				return 0.7f;
			case 2:
				return 0.49f;
			case 3:
				return 0.343f;
			default:
				return ( float )Math.pow( 0.7f, fatigueLevel );
		}
	}
}
