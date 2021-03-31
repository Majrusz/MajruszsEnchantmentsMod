package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.*;
import com.wonderfulenchantments.enchantments.*;
import net.minecraftforge.fml.ModLoadingContext;

public class Instances {
	// Fishing Rod Enchantments
	public static final FanaticEnchantment FISHING_FANATIC;

	// Sword Enchantments
	public static final HumanSlayerEnchantment HUMAN_SLAYER;
	public static final PufferfishVengeanceEnchantment PUFFERFISH_VENGEANCE;
	public static final LeechEnchantment LEECH;

	// Armor Enchantments
	public static final DodgeEnchantment DODGE;
	public static final EnlightenmentEnchantment ENLIGHTENMENT;
	public static final PhoenixDiveEnchantment PHOENIX_DIVE;
	public static final MagicProtectionEnchantment MAGIC_PROTECTION;
	public static final SixthSenseEnchantment SIXTH_SENSE;
	public static final MithridatismEnchantment MITHRIDATISM;

	// Shield Enchantments
	public static final VitalityEnchantment VITALITY;
	public static final ImmortalityEnchantment IMMORTALITY;
	public static final AbsorberEnchantment ABSORBER;

	// Tool Enchantments
	public static final SmelterEnchantment SMELTER;
	public static final GottaMineFastEnchantment GOTTA_MINE_FAST;
	public static final TelekinesisEnchantment TELEKINESIS;
	public static final HarvesterEnchantment HARVESTER;

	// Horse Armor Enchantments
	public static final SwiftnessEnchantment SWIFTNESS;
	public static final HorseProtectionEnchantment HORSE_PROTECTION;
	public static final HorseFrostWalkerEnchantment HORSE_FROST_WALKER;

	// Bow Enchantments
	public static final HunterEnchantment HUNTER;

	// Trident Enchantments
	public static final ElderGaurdianFavorEnchantment ELDER_GAURDIAN_FAVOR;

	// Curses
	public static final SlownessCurse SLOWNESS;
	public static final FatigueCurse FATIGUE;
	public static final IncompatibilityCurse INCOMPATIBILITY;
	public static final VampirismCurse VAMPIRISM;
	public static final CorrosionCurse CORROSION;

	// Potion effects
	public static final MithridatismEnchantment.MithridatismProtectionEffect MITHRIDATISM_PROTECTION;

	// Client-side effects
	public static final ClientEffects CLIENT_EFFECTS;

	static {
		FISHING_FANATIC = new FanaticEnchantment();

		HUMAN_SLAYER = new HumanSlayerEnchantment();
		PUFFERFISH_VENGEANCE = new PufferfishVengeanceEnchantment();
		LEECH = new LeechEnchantment();

		DODGE = new DodgeEnchantment();
		ENLIGHTENMENT = new EnlightenmentEnchantment();
		PHOENIX_DIVE = new PhoenixDiveEnchantment();
		MAGIC_PROTECTION = new MagicProtectionEnchantment();
		SIXTH_SENSE = new SixthSenseEnchantment();
		MITHRIDATISM = new MithridatismEnchantment();

		VITALITY = new VitalityEnchantment();
		IMMORTALITY = new ImmortalityEnchantment();
		ABSORBER = new AbsorberEnchantment();

		SMELTER = new SmelterEnchantment();
		GOTTA_MINE_FAST = new GottaMineFastEnchantment();
		TELEKINESIS = new TelekinesisEnchantment();
		HARVESTER = new HarvesterEnchantment();

		SWIFTNESS = new SwiftnessEnchantment();
		HORSE_PROTECTION = new HorseProtectionEnchantment();
		HORSE_FROST_WALKER = new HorseFrostWalkerEnchantment();

		HUNTER = new HunterEnchantment();

		ELDER_GAURDIAN_FAVOR = new ElderGaurdianFavorEnchantment();

		SLOWNESS = new SlownessCurse();
		FATIGUE = new FatigueCurse();
		INCOMPATIBILITY = new IncompatibilityCurse();
		VAMPIRISM = new VampirismCurse();
		CORROSION = new CorrosionCurse();

		MITHRIDATISM_PROTECTION = new MithridatismEnchantment.MithridatismProtectionEffect( MITHRIDATISM );

		CLIENT_EFFECTS = new ClientEffects();

		WonderfulEnchantments.CONFIG_HANDLER.register( ModLoadingContext.get() );
		WonderfulEnchantments.CONFIG_HANDLER_CLIENT.register( ModLoadingContext.get() );
	}
}
