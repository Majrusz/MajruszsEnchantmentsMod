package com.majruszsenchantments;

import com.majruszsenchantments.curses.*;
import com.majruszsenchantments.enchantments.*;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.annotations.AnnotationHandler;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.items.ItemHelper;
import com.mlib.registries.RegistryHelper;
import com.mlib.triggers.BasicTrigger;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

import static com.majruszsenchantments.MajruszsEnchantments.CLIENT_CONFIG;
import static com.majruszsenchantments.MajruszsEnchantments.SERVER_CONFIG;

public class Registries {
	private static final RegistryHelper HELPER = new RegistryHelper( MajruszsEnchantments.MOD_ID );
	public static final List< GameModifier > GAME_MODIFIERS;

	static {
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.ENCHANTMENT ).name( "Enchantments" );
		GameModifier.addNewGroup( SERVER_CONFIG, Modifiers.CURSE ).name( "Curses" );
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
	public static final RegistryObject< SmelterEnchantment > SMELTER = ENCHANTMENTS.register( "smelter", SmelterEnchantment::new );
	public static final RegistryObject< TelekinesisEnchantment > TELEKINESIS = ENCHANTMENTS.register( "telekinesis", TelekinesisEnchantment::new );

	// Curses
	public static final RegistryObject< BreakingCurse > BREAKING = ENCHANTMENTS.register( "breaking_curse", BreakingCurse::new );
	public static final RegistryObject< CorrosionCurse > CORROSION = ENCHANTMENTS.register( "corrosion_curse", CorrosionCurse::new );
	public static final RegistryObject< FatigueCurse > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse::new );
	public static final RegistryObject< IncompatibilityCurse > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse::new );
	public static final RegistryObject< VampirismCurse > VAMPIRISM = ENCHANTMENTS.register( "vampirism_curse", VampirismCurse::new );

	// Particles
	public static final RegistryObject< SimpleParticleType > DODGE_PARTICLE = PARTICLE_TYPES.register( "dodge_particle", ()->new SimpleParticleType( true ) );
	public static final RegistryObject< SimpleParticleType > TELEKINESIS_PARTICLE = PARTICLE_TYPES.register( "telekinesis_particle", ()->new SimpleParticleType( true ) );

	// Triggers
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( HELPER );

	static {
		// must stay below all instances because otherwise modifiers can access registry objects too fast
		AnnotationHandler annotationHandler = new AnnotationHandler( MajruszsEnchantments.MOD_ID );
		GAME_MODIFIERS = annotationHandler.getInstances( GameModifier.class );
	}

	public static ResourceLocation getLocation( String register ) {
		return HELPER.getLocation( register );
	}

	public static String getLocationString( String register ) {
		return getLocation( register ).toString();
	}

	public static void initialize() {
		FMLJavaModLoadingContext modLoadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = modLoadingContext.getModEventBus();

		HELPER.registerAll();
		modEventBus.addListener( PacketHandler::registerPacket );
		DistExecutor.unsafeRunWhenOn( Dist.CLIENT, ()->RegistriesClient::initialize );
		ItemHelper.addEnchantmentTypesToItemGroup( CreativeModeTab.TAB_COMBAT, SHIELD, BOW_AND_CROSSBOW, MELEE_MINECRAFT, MELEE );
		ItemHelper.addEnchantmentTypesToItemGroup( CreativeModeTab.TAB_TOOLS, HOE, GOLDEN, TOOLS );
		ItemHelper.addEnchantmentTypeToItemGroup( CreativeModeTab.TAB_MISC, HORSE_ARMOR );

		SERVER_CONFIG.register( ModLoadingContext.get() );
		CLIENT_CONFIG.register( ModLoadingContext.get() );
	}

	public static < Type extends EnchantmentModifier< ? > > Supplier< Boolean > getEnabledSupplier( Class< Type > clazz ) {
		var enchantmentModifier = GAME_MODIFIERS.stream()
			.filter( modifier->clazz.equals( modifier.getClass() ) )
			.findFirst()
			.orElseThrow();

		return clazz.cast( enchantmentModifier ).getEnabledSupplier();
	}

	public static class Modifiers {
		public static final String ENCHANTMENT = Registries.getLocationString( "enchantment" );
		public static final String CURSE = Registries.getLocationString( "curse" );
	}
}
