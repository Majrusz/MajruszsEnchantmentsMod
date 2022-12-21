package com.majruszsenchantments;

import com.majruszsenchantments.curses.*;
import com.majruszsenchantments.enchantments.*;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.gamemodifiers.GameModifier;
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

import java.util.ArrayList;
import java.util.List;

import static com.majruszsenchantments.MajruszsEnchantments.CLIENT_CONFIG;
import static com.majruszsenchantments.MajruszsEnchantments.SERVER_CONFIG;

public class Registries {
	private static final RegistryHelper HELPER = new RegistryHelper( MajruszsEnchantments.MOD_ID );
	public static final List< GameModifier > GAME_MODIFIERS = new ArrayList<>();

	static {
		SERVER_CONFIG.addGroup( GameModifier.addNewGroup( EnchantmentModifier.ENCHANTMENT, "Enchantments", "" ) );
		SERVER_CONFIG.addGroup( GameModifier.addNewGroup( EnchantmentModifier.CURSE, "Curses", "" ) );
	}

	// Groups
	static final DeferredRegister< Enchantment > ENCHANTMENTS = HELPER.create( ForgeRegistries.Keys.ENCHANTMENTS );
	static final DeferredRegister< ParticleType< ? > > PARTICLE_TYPES = HELPER.create( ForgeRegistries.Keys.PARTICLE_TYPES );

	// Enchantment Categories
	public static final EnchantmentCategory BOW_AND_CROSSBOW = EnchantmentCategory.create( "bow_and_crossbow", item->item instanceof BowItem || item instanceof CrossbowItem );
	public static final EnchantmentCategory GOLDEN = EnchantmentCategory.create( "golden", item->item instanceof DiggerItem diggerItem && diggerItem.getTier() == Tiers.GOLD || item instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.GOLD );
	public static final EnchantmentCategory HORSE_ARMOR = EnchantmentCategory.create( "horse_armor", item->item instanceof HorseArmorItem );
	public static final EnchantmentCategory HOE = EnchantmentCategory.create( "hoe", item->item instanceof HoeItem );
	public static final EnchantmentCategory MELEE_MINECRAFT = EnchantmentCategory.create( "melee_minecraft", item->item instanceof SwordItem || item instanceof AxeItem ); // for some reason all minecraft sword enchantments are applicable to axes
	public static final EnchantmentCategory MELEE = EnchantmentCategory.create( "melee_weapon", item->MELEE_MINECRAFT.canEnchant( item ) || item instanceof TridentItem );
	public static final EnchantmentCategory SHIELD = EnchantmentCategory.create( "shield", item->item instanceof ShieldItem );
	public static final EnchantmentCategory TOOLS = EnchantmentCategory.create( "tools", item->MELEE.canEnchant( item ) || EnchantmentCategory.DIGGER.canEnchant( item ) || BOW_AND_CROSSBOW.canEnchant( item ) );

	// Enchantments
	public static final RegistryObject< DodgeEnchantment > DODGE = ENCHANTMENTS.register( "dodge", DodgeEnchantment.create() );
	public static final RegistryObject< DeathWishEnchantment > DEATH_WISH = ENCHANTMENTS.register( "death_wish", DeathWishEnchantment.create() );
	public static final RegistryObject< EnlightenmentEnchantment > ENLIGHTENMENT = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment.create() );
	public static final RegistryObject< FishingFanaticEnchantment > FISHING_FANATIC = ENCHANTMENTS.register( "fishing_fanatic", FishingFanaticEnchantment.create() );
	public static final RegistryObject< FuseCutterEnchantment > FUSE_CUTTER = ENCHANTMENTS.register( "fuse_cutter", FuseCutterEnchantment.create() );
	public static final RegistryObject< GoldFuelledEnchantment > GOLD_FUELLED = ENCHANTMENTS.register( "gold_fuelled", GoldFuelledEnchantment.create() );
	public static final RegistryObject< HunterEnchantment > HUNTER = ENCHANTMENTS.register( "hunter", HunterEnchantment.create() );
	public static final RegistryObject< HarvesterEnchantment > HARVESTER = ENCHANTMENTS.register( "harvester", HarvesterEnchantment.create() );
	public static final RegistryObject< HorseFrostWalkerEnchantment > HORSE_FROST_WALKER = ENCHANTMENTS.register( "horse_frost_walker", HorseFrostWalkerEnchantment.create() );
	public static final RegistryObject< HorseProtectionEnchantment > HORSE_PROTECTION = ENCHANTMENTS.register( "horse_protection", HorseProtectionEnchantment.create() );
	public static final RegistryObject< HorseSwiftnessEnchantment > HORSE_SWIFTNESS = ENCHANTMENTS.register( "horse_swiftness", HorseSwiftnessEnchantment.create() );
	public static final RegistryObject< ImmortalityEnchantment > IMMORTALITY = ENCHANTMENTS.register( "immortality", ImmortalityEnchantment.create() );
	public static final RegistryObject< LeechEnchantment > LEECH = ENCHANTMENTS.register( "leech", LeechEnchantment.create() );
	public static final RegistryObject< MagicProtectionEnchantment > MAGIC_PROTECTION = ENCHANTMENTS.register( "magic_protection", MagicProtectionEnchantment.create() );
	public static final RegistryObject< MisanthropyEnchantment > MISANTHROPY = ENCHANTMENTS.register( "misanthropy", MisanthropyEnchantment.create() );
	public static final RegistryObject< SmelterEnchantment > SMELTER = ENCHANTMENTS.register( "smelter", SmelterEnchantment.create() );
	public static final RegistryObject< TelekinesisEnchantment > TELEKINESIS = ENCHANTMENTS.register( "telekinesis", TelekinesisEnchantment.create() );
	public static final RegistryObject< VitalityEnchantment > VITALITY = ENCHANTMENTS.register( "vitality", VitalityEnchantment.create() );

	// Curses
	public static final RegistryObject< BreakingCurse > BREAKING = ENCHANTMENTS.register( "breaking_curse", BreakingCurse.create() );
	public static final RegistryObject< CorrosionCurse > CORROSION = ENCHANTMENTS.register( "corrosion_curse", CorrosionCurse.create() );
	public static final RegistryObject< FatigueCurse > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse.create() );
	public static final RegistryObject< IncompatibilityCurse > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse.create() );
	public static final RegistryObject< VampirismCurse > VAMPIRISM = ENCHANTMENTS.register( "vampirism_curse", VampirismCurse.create() );

	// Particles
	public static final RegistryObject< SimpleParticleType > DODGE_PARTICLE = PARTICLE_TYPES.register( "dodge_particle", ()->new SimpleParticleType( true ) );

	// Triggers
	public static final BasicTrigger BASIC_TRIGGER = BasicTrigger.createRegisteredInstance( HELPER );

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

		SERVER_CONFIG.register( ModLoadingContext.get() );
		CLIENT_CONFIG.register( ModLoadingContext.get() );
	}
}
