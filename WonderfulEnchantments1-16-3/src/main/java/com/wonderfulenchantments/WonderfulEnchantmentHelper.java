package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class WonderfulEnchantmentHelper {
	public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item item )->item instanceof ShieldItem );
	private static final int disableEnchantmentValue = 9001;

	public static void addTypeToItemGroup( EnchantmentType type, ItemGroup itemGroup ) {
		EnchantmentType[] group = itemGroup.getRelevantEnchantmentTypes();
		EnchantmentType[] temporary = new EnchantmentType[ group.length + 1 ];
		System.arraycopy( group, 0, temporary, 0, group.length );
		temporary[ group.length - 1 ] = type;
		itemGroup.setRelevantEnchantmentTypes( temporary );
	}

	public static < InstanceType > int calculateEnchantmentSumIfIsInstanceOf( Enchantment enchantment, LivingEntity livingEntity, EquipmentSlotType[] slotTypes, Class< InstanceType > type ) {
		int sum = 0;

		for( EquipmentSlotType slotType : slotTypes ) {
			ItemStack itemStack = livingEntity.getItemStackFromSlot( slotType );
			if( type.isInstance( itemStack.getItem() ) )
				sum += EnchantmentHelper.getEnchantmentLevel( enchantment, itemStack );
		}

		return sum;
	}

	public static int calculateEnchantmentSum( Enchantment enchantment, LivingEntity livingEntity, EquipmentSlotType[] slotTypes ) {
		int sum = 0;

		for( EquipmentSlotType slotType : slotTypes )
			sum += EnchantmentHelper.getEnchantmentLevel( enchantment, livingEntity.getItemStackFromSlot( slotType ) );

		return sum;
	}

	public static int increaseLevelIfEnchantmentIsDisabled( Enchantment enchantment ) {
		if( enchantment instanceof FanaticEnchantment )
			return ( ConfigHandler.Values.FISHING_FANATIC.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof HumanSlayerEnchantment )
			return ( ConfigHandler.Values.HUMAN_SLAYER.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof DodgeEnchantment )
			return ( ConfigHandler.Values.DODGE.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof EnlightenmentEnchantment )
			return ( ConfigHandler.Values.ENLIGHTENMENT.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof VitalityEnchantment )
			return ( ConfigHandler.Values.VITALITY.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof PhoenixDiveEnchantment )
			return ( ConfigHandler.Values.PHOENIX_DIVE.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof PufferfishVengeanceEnchantment )
			return ( ConfigHandler.Values.PUFFERFISH_VENGEANCE.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof ImmortalityEnchantment )
			return ( ConfigHandler.Values.IMMORTALITY.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof SmelterEnchantment )
			return ( ConfigHandler.Values.SMELTER.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof SlownessCurse )
			return ( ConfigHandler.Values.SLOWNESS.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof FatigueCurse )
			return ( ConfigHandler.Values.FATIGUE.get() ? 0 : disableEnchantmentValue );

		return 0;
	}
}
