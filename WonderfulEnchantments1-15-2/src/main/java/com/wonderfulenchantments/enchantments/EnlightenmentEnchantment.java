package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

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
		return 6 + enchantmentLevel * 12 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		int enlightenmentSum = WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.ENLIGHTENMENT.get(), event.getPlayer(), EquipmentSlotTypes.ARMOR );

		if( enlightenmentSum > 0 ) {
			double bonusRatio = 0.25D * ( double )enlightenmentSum;
			double randomBonus = bonusRatio * WonderfulEnchantments.RANDOM.nextDouble();
			int bonusExp = ( int )( Math.round( randomBonus * ( double )event.getOrb().getXpValue() ) );

			event.getPlayer().giveExperiencePoints( bonusExp );
		}
	}

	@SubscribeEvent
	public static void onCalculatingEnchantmentLevels( EnchantmentLevelSetEvent event ) {
		BlockPos position = event.getPos();
		PlayerEntity player = event.getWorld().getClosestPlayer( position.getX(), position.getY(), position.getZ() );

		Enchantment enchantment = RegistryHandler.ENLIGHTENMENT.get();
		int enlightenmentSum = WonderfulEnchantmentHelper.calculateEnchantmentSum( enchantment, player, EquipmentSlotTypes.ARMOR );

		if( enlightenmentSum > 0 ) {
			int bonus = Math.max( 0, Math.min( event.getLevel() * enlightenmentSum / ( enchantment.getMaxLevel() * 4 ), 30 - event.getLevel() ) ); // gives 0%-100% level bonus (capped at 30lvl)

			event.setLevel( event.getLevel() + bonus );
		}
	}
}