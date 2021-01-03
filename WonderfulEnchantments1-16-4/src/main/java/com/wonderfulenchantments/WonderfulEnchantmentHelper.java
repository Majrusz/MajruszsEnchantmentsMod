package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.curses.VampirismCurse;
import com.wonderfulenchantments.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class WonderfulEnchantmentHelper {
	public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item item )->item instanceof ShieldItem );
	public static final EnchantmentType HORSE_ARMOR = EnchantmentType.create( "horse_armor", ( Item item )->item instanceof HorseArmorItem );
	private static final int disableEnchantmentValue = 9001;

	public static void addTypeToItemGroup( EnchantmentType type, ItemGroup itemGroup ) {
		EnchantmentType[] group = itemGroup.getRelevantEnchantmentTypes();
		if( group.length == 0 ) {
			itemGroup.setRelevantEnchantmentTypes( type );
			return;
		}
		EnchantmentType[] temporary = new EnchantmentType[ group.length + 1 ];
		System.arraycopy( group, 0, temporary, 0, group.length );
		temporary[ group.length - 1 ] = type;
		itemGroup.setRelevantEnchantmentTypes( temporary );
	}

	public static < InstanceType > int calculateEnchantmentSumIfIsInstanceOf( Enchantment enchantment, LivingEntity livingEntity,
		EquipmentSlotType[] slotTypes, Class< InstanceType > type
	) {
		int sum = 0;

		for( EquipmentSlotType slotType : slotTypes ) {
			ItemStack itemStack = livingEntity.getItemStackFromSlot( slotType );
			if( type.isInstance( itemStack.getItem() ) )
				sum += EnchantmentHelper.getEnchantmentLevel( enchantment, itemStack );
		}

		return sum;
	}

	public static < InstanceType > int calculateEnchantmentSumIfIsInstanceOf( Enchantment enchantment, Iterable< ItemStack > itemStacks,
		Class< InstanceType > type
	) {
		int sum = 0;

		for( ItemStack itemStack : itemStacks )
			if( type.isInstance( itemStack.getItem() ) )
				sum += EnchantmentHelper.getEnchantmentLevel( enchantment, itemStack );

		return sum;
	}

	public static int calculateEnchantmentSum( Enchantment enchantment, LivingEntity livingEntity, EquipmentSlotType[] slotTypes ) {
		int sum = 0;

		for( EquipmentSlotType slotType : slotTypes )
			sum += EnchantmentHelper.getEnchantmentLevel( enchantment, livingEntity.getItemStackFromSlot( slotType ) );

		return sum;
	}

	public static int calculateEnchantmentSum( Enchantment enchantment, Iterable< ItemStack > itemStacks ) {
		int sum = 0;

		for( ItemStack itemStack : itemStacks )
			sum += EnchantmentHelper.getEnchantmentLevel( enchantment, itemStack );

		return sum;
	}

	public static final int ticksInSecond = 20;

	public static int secondsToTicks( double seconds ) {
		return ( int )( seconds * ticksInSecond );
	}

	public static final int ticksInMinute = ticksInSecond * 60;

	public static int minutesToTicks( double minutes ) {
		return ( int )( minutes * ticksInMinute );
	}

	public static boolean isDirectDamageFromLivingEntity( DamageSource source ) {
		return source.getTrueSource() instanceof LivingEntity && source.getImmediateSource() instanceof LivingEntity;
	}

	public static boolean isHorseArmor( ItemStack itemStack ) {
		return ( itemStack.getItem() instanceof HorseArmorItem || itemStack.getItem() instanceof DyeableHorseArmorItem );
	}

	public static int increaseLevelIfEnchantmentIsDisabled( Enchantment enchantment ) {
		Function< ForgeConfigSpec.BooleanValue, Integer > checkEnchantment = ( value )->( value.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof FanaticEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.FISHING_FANATIC );

		if( enchantment instanceof HumanSlayerEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.HUMAN_SLAYER );

		if( enchantment instanceof DodgeEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.DODGE );

		if( enchantment instanceof EnlightenmentEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.ENLIGHTENMENT );

		if( enchantment instanceof VitalityEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.VITALITY );

		if( enchantment instanceof PhoenixDiveEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.PHOENIX_DIVE );

		if( enchantment instanceof PufferfishVengeanceEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.PUFFERFISH_VENGEANCE );

		if( enchantment instanceof ImmortalityEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.IMMORTALITY );

		if( enchantment instanceof SmelterEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.SMELTER );

		if( enchantment instanceof GottaMineFastEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.GOTTA_MINE_FAST );

		if( enchantment instanceof LeechEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.LEECH );

		if( enchantment instanceof MagicProtectionEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.MAGIC_PROTECTION );

		if( enchantment instanceof SwiftnessEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.SWIFTNESS );

		if( enchantment instanceof HorseProtectionEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.HORSE_PROTECTION );

		if( enchantment instanceof HorseFrostWalkerEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.HORSE_FROST_WALKER );

		if( enchantment instanceof TelekinesisEnchantment )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.TELEKINESIS );

		if( enchantment instanceof SlownessCurse )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.SLOWNESS );

		if( enchantment instanceof FatigueCurse )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.FATIGUE );

		if( enchantment instanceof IncompatibilityCurse )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.INCOMPATIBILITY );

		if( enchantment instanceof VampirismCurse )
			return checkEnchantment.apply( ConfigHandler.Config.Enchantability.VAMPIRISM );

		return 0;
	}
}
