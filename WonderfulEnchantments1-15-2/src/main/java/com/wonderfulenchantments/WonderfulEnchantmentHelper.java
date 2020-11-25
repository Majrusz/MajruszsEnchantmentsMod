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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

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

	public static final int ticksInSecond = 20;
	public static int secondsToTicks( double seconds ) {
		return (int)( seconds*ticksInSecond );
	}

	public static final int ticksInMinute = ticksInSecond*60;
	public static int minutesToTicks( double minutes ) {
		return (int)( minutes*ticksInMinute );
	}

	public static boolean isDirectDamageFromLivingEntity( DamageSource source ) {
		return source.getTrueSource() instanceof LivingEntity && source.getImmediateSource() instanceof LivingEntity;
	}

	public static int increaseLevelIfEnchantmentIsDisabled( Enchantment enchantment ) {
		Function< ForgeConfigSpec.BooleanValue, Integer > checkEnchantment = ( value )->( value.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof FanaticEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.FISHING_FANATIC );

		if( enchantment instanceof HumanSlayerEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.HUMAN_SLAYER );

		if( enchantment instanceof DodgeEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.DODGE );

		if( enchantment instanceof EnlightenmentEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.ENLIGHTENMENT );

		if( enchantment instanceof VitalityEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.VITALITY );

		if( enchantment instanceof PhoenixDiveEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.PHOENIX_DIVE );

		if( enchantment instanceof PufferfishVengeanceEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.PUFFERFISH_VENGEANCE );

		if( enchantment instanceof ImmortalityEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.IMMORTALITY );

		if( enchantment instanceof SmelterEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.SMELTER );

		if( enchantment instanceof GottaMineFastEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.GOTTA_MINE_FAST );

		if( enchantment instanceof LeechEnchantment )
			return checkEnchantment.apply( ConfigHandler.Values.LEECH );

		if( enchantment instanceof SlownessCurse )
			return checkEnchantment.apply( ConfigHandler.Values.SLOWNESS );

		if( enchantment instanceof FatigueCurse )
			return checkEnchantment.apply( ConfigHandler.Values.FATIGUE );

		return 0;
	}
}
