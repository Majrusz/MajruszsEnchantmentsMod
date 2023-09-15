package com.majruszsenchantments;

import com.majruszsenchantments.curses.*;
import com.majruszsenchantments.enchantments.*;
import com.mlib.config.ConfigHandler;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.items.ItemHelper;
import com.mlib.modhelper.ModHelper;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registries {
	public static final ModHelper HELPER = ModHelper.create( MajruszsEnchantments.MOD_ID );

	// Configs
	public static final ConfigHandler SERVER_CONFIG = HELPER.createConfig( ModConfig.Type.SERVER );

	static {
		ModConfigs.init( SERVER_CONFIG, Groups.ENCHANTMENT ).name( "Enchantments" );
		ModConfigs.init( SERVER_CONFIG, Groups.CURSE ).name( "Curses" );
	}

	// Groups
	static final DeferredRegister< Enchantment > ENCHANTMENTS = HELPER.create( ForgeRegistries.Keys.ENCHANTMENTS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );

	// Enchantment Categories
	public static final EnchantmentCategory BOW_AND_CROSSBOW = EnchantmentCategory.create( "bow_and_crossbow", ItemHelper::isRangedWeapon );
	public static final EnchantmentCategory GOLDEN = EnchantmentCategory.create( "golden", item->ItemHelper.isGoldenTool( item ) || ItemHelper.isGoldenArmor( item ) );
	public static final EnchantmentCategory HORSE_ARMOR = EnchantmentCategory.create( "horse_armor", item->item instanceof HorseArmorItem );
	public static final EnchantmentCategory HOE = EnchantmentCategory.create( "hoe", item->item instanceof HoeItem );
	public static final EnchantmentCategory MELEE_MINECRAFT = EnchantmentCategory.create( "melee_minecraft", item->item instanceof SwordItem || item instanceof AxeItem ); // for some reason all minecraft sword enchantments are applicable to axes
	public static final EnchantmentCategory MELEE = EnchantmentCategory.create( "melee_weapon", ItemHelper::isMeleeWeapon );
	public static final EnchantmentCategory SHIELD = EnchantmentCategory.create( "shield", ItemHelper::isShield );
	public static final EnchantmentCategory TOOLS = EnchantmentCategory.create( "tools", ItemHelper::isAnyTool );

	// Enchantments
	public static final RegistryObject< DodgeEnchantment > DODGE = ENCHANTMENTS.register( "dodge", DodgeEnchantment::new );
	public static final RegistryObject< DeathWishEnchantment > DEATH_WISH = ENCHANTMENTS.register( "death_wish", DeathWishEnchantment::new );
	public static final RegistryObject< EnlightenmentEnchantment > ENLIGHTENMENT = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment::new );
	public static final RegistryObject< FishingFanaticEnchantment > FISHING_FANATIC = ENCHANTMENTS.register( "fishing_fanatic", FishingFanaticEnchantment::new );
	public static final RegistryObject< FuseCutterEnchantment > FUSE_CUTTER = ENCHANTMENTS.register( "fuse_cutter", FuseCutterEnchantment::new );
	public static final RegistryObject< GoldFuelledEnchantment > GOLD_FUELLED = ENCHANTMENTS.register( "gold_fuelled", GoldFuelledEnchantment::new );
	public static final RegistryObject< HunterEnchantment > HUNTER = ENCHANTMENTS.register( "hunter", HunterEnchantment::new );
	public static final RegistryObject< HarvesterEnchantment > HARVESTER = ENCHANTMENTS.register( "harvester", HarvesterEnchantment::new );
	public static final RegistryObject< HorseFrostWalkerEnchantment > HORSE_FROST_WALKER = ENCHANTMENTS.register( "horse_frost_walker", HorseFrostWalkerEnchantment::new );
	public static final RegistryObject< HorseProtectionEnchantment > HORSE_PROTECTION = ENCHANTMENTS.register( "horse_protection", HorseProtectionEnchantment::new );
	public static final RegistryObject< HorseSwiftnessEnchantment > HORSE_SWIFTNESS = ENCHANTMENTS.register( "horse_swiftness", HorseSwiftnessEnchantment::new );
	public static final RegistryObject< ImmortalityEnchantment > IMMORTALITY = ENCHANTMENTS.register( "immortality", ImmortalityEnchantment::new );
	public static final RegistryObject< LeechEnchantment > LEECH = ENCHANTMENTS.register( "leech", LeechEnchantment::new );
	public static final RegistryObject< MagicProtectionEnchantment > MAGIC_PROTECTION = ENCHANTMENTS.register( "magic_protection", MagicProtectionEnchantment::new );
	public static final RegistryObject< MisanthropyEnchantment > MISANTHROPY = ENCHANTMENTS.register( "misanthropy", MisanthropyEnchantment::new );
	public static final RegistryObject< RepulsionEnchantment > REPULSION = ENCHANTMENTS.register( "repulsion", RepulsionEnchantment::new );
	public static final RegistryObject< SixthSenseEnchantment > SIXTH_SENSE = ENCHANTMENTS.register( "sixth_sense", SixthSenseEnchantment::new );
	public static final RegistryObject< SmelterEnchantment > SMELTER = ENCHANTMENTS.register( "smelter", SmelterEnchantment::new );
	public static final RegistryObject< TelekinesisEnchantment > TELEKINESIS = ENCHANTMENTS.register( "telekinesis", TelekinesisEnchantment::new );

	// Curses
	public static final RegistryObject< BreakingCurse > BREAKING = ENCHANTMENTS.register( "breaking_curse", BreakingCurse::new );
	public static final RegistryObject< CorrosionCurse > CORROSION = ENCHANTMENTS.register( "corrosion_curse", CorrosionCurse::new );
	public static final RegistryObject< FatigueCurse > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse::new );
	public static final RegistryObject< IncompatibilityCurse > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse::new );
	public static final RegistryObject< SlipperyCurse > SLIPPERY = ENCHANTMENTS.register( "slippery_curse", SlipperyCurse::new );
	public static final RegistryObject< VampirismCurse > VAMPIRISM = ENCHANTMENTS.register( "vampirism_curse", VampirismCurse::new );

	// Particles
	public static final RegistryObject< SimpleParticleType > DODGE_PARTICLE = PARTICLE_TYPES.register( "dodge_particle", ()->new SimpleParticleType( true ) );
	public static final RegistryObject< SimpleParticleType > TELEKINESIS_PARTICLE = PARTICLE_TYPES.register( "telekinesis_particle", ()->new SimpleParticleType( true ) );

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return getLocation( register ).toString();
	}

	public static void initialize() {
		HELPER.register();
	}

	public static class Groups {
		public static final String ENCHANTMENT = Registries.getLocationString( "enchantment" );
		public static final String CURSE = Registries.getLocationString( "curse" );
	}
}
