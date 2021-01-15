package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.curses.VampirismCurse;
import com.wonderfulenchantments.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class WonderfulEnchantmentHelper {
	public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item item )->item instanceof ShieldItem );
	public static final EnchantmentType HORSE_ARMOR = EnchantmentType.create( "horse_armor", ( Item item )->item instanceof HorseArmorItem );
	private static final int disableEnchantmentValue = 9001;

	public static boolean isDirectDamageFromLivingEntity( DamageSource source ) {
		return source.getTrueSource() instanceof LivingEntity && source.getImmediateSource() instanceof LivingEntity;
	}

	public static int increaseLevelIfEnchantmentIsDisabled( Enchantment enchantment ) {
		Function< ForgeConfigSpec.BooleanValue, Integer > checkEnchantment = ( value )->( value.get() ? 0 : disableEnchantmentValue );

		if( enchantment instanceof FanaticEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.FISHING_FANATIC );

		if( enchantment instanceof HumanSlayerEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.HUMAN_SLAYER );

		if( enchantment instanceof DodgeEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.DODGE );

		if( enchantment instanceof EnlightenmentEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.ENLIGHTENMENT );

		if( enchantment instanceof VitalityEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.VITALITY );

		if( enchantment instanceof PhoenixDiveEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.PHOENIX_DIVE );

		if( enchantment instanceof PufferfishVengeanceEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.PUFFERFISH_VENGEANCE );

		if( enchantment instanceof ImmortalityEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.IMMORTALITY );

		if( enchantment instanceof SmelterEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.SMELTER );

		if( enchantment instanceof GottaMineFastEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.GOTTA_MINE_FAST );

		if( enchantment instanceof LeechEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.LEECH );

		if( enchantment instanceof MagicProtectionEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.MAGIC_PROTECTION );

		if( enchantment instanceof SwiftnessEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.SWIFTNESS );

		if( enchantment instanceof HorseProtectionEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.HORSE_PROTECTION );

		if( enchantment instanceof HorseFrostWalkerEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.HORSE_FROST_WALKER );

		if( enchantment instanceof TelekinesisEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.TELEKINESIS );

		if( enchantment instanceof AbsorberEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.ABSORBER );

		if( enchantment instanceof HunterEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.HUNTER );

		if( enchantment instanceof ElderGaurdianFavorEnchantment )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.ELDER_GUARDIAN_FAVOR );

		if( enchantment instanceof SlownessCurse )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.SLOWNESS );

		if( enchantment instanceof FatigueCurse )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.FATIGUE );

		if( enchantment instanceof IncompatibilityCurse )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.INCOMPATIBILITY );

		if( enchantment instanceof VampirismCurse )
			return checkEnchantment.apply( ConfigHandlerOld.Config.Enchantability.VAMPIRISM );

		return 0;
	}
}
