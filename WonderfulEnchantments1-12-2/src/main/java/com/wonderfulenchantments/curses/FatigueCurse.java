package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDigging;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class FatigueCurse extends Enchantment {
	public FatigueCurse( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.DIGGER, EquipmentSlotTypes.ARMOR_AND_HANDS );

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
		return 10 + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 40;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof EnchantmentDigging ) && super.canApplyTogether( enchantment );
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
		int fatigueLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.FATIGUE, event.getEntityPlayer() );

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