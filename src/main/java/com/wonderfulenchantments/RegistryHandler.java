package com.wonderfulenchantments;

import com.mlib.items.ItemHelper;
import com.wonderfulenchantments.items.DyeableHorseArmorItemReplacement;
import com.wonderfulenchantments.items.HorseArmorItemReplacement;
import com.wonderfulenchantments.items.ShieldItemReplacement;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Class responsible for registering all objects. (items, enchantments etc.)*/
public class RegistryHandler {
	public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS,
		WonderfulEnchantments.MOD_ID
	);
	public static final DeferredRegister< ParticleType< ? > > PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES,
		WonderfulEnchantments.MOD_ID
	);
	public static final DeferredRegister< Item > ITEMS_TO_REPLACE = DeferredRegister.create( ForgeRegistries.ITEMS, "minecraft" );

	public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item item )->item instanceof ShieldItem );
	public static final EnchantmentType HORSE_ARMOR = EnchantmentType.create( "horse_armor", ( Item item )->item instanceof HorseArmorItem );
	public static final EnchantmentType BOW_AND_CROSSBOW = EnchantmentType.create( "bow_and_crossbow", ( Item item )->( item instanceof BowItem || item instanceof CrossbowItem ) );

	public static final RegistryObject< BasicParticleType > PHOENIX_PARTICLE = PARTICLES.register( "phoenix_particle",
		()->new BasicParticleType( true )
	);

	/** General initialization of all objects. */
	public static void init() {
		FMLJavaModLoadingContext modLoadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = modLoadingContext.getModEventBus();

		addEnchantments( modEventBus );
		replaceRestStandardMinecraftItems( modEventBus );
		PARTICLES.register( modEventBus );
		addEnchantmentTypesToItemGroups();
		modEventBus.addListener( RegistryHandler::doClientSetup );
		modEventBus.addListener( PacketHandler::registerPacket );
	}

	/** Replacing standard minecraft items with the new ones which could be enchanted. */
	private static void replaceRestStandardMinecraftItems( final IEventBus modEventBus ) {
		ITEMS_TO_REPLACE.register( "shield", ShieldItemReplacement::new );
		ITEMS_TO_REPLACE.register( "leather_horse_armor", ()->new DyeableHorseArmorItemReplacement( 3, "leather" ) );
		ITEMS_TO_REPLACE.register( "iron_horse_armor", ()->new HorseArmorItemReplacement( 5, "iron" ) );
		ITEMS_TO_REPLACE.register( "golden_horse_armor", ()->new HorseArmorItemReplacement( 7, "gold" ) );
		ITEMS_TO_REPLACE.register( "diamond_horse_armor", ()->new HorseArmorItemReplacement( 11, "diamond" ) );
		ITEMS_TO_REPLACE.register( modEventBus );
	}

	/** Registering all enchantments. */
	private static void addEnchantments( final IEventBus modEventBus ) {
		ENCHANTMENTS.register( "fishing_fanatic", ()->Instances.FISHING_FANATIC );
		ENCHANTMENTS.register( "human_slayer", ()->Instances.HUMAN_SLAYER );
		ENCHANTMENTS.register( "pufferfish_vengeance", ()->Instances.PUFFERFISH_VENGEANCE );
		ENCHANTMENTS.register( "leech", ()->Instances.LEECH );
		ENCHANTMENTS.register( "dodge", ()->Instances.DODGE );
		ENCHANTMENTS.register( "enlightenment", ()->Instances.ENLIGHTENMENT );
		ENCHANTMENTS.register( "phoenix_dive", ()->Instances.PHOENIX_DIVE );
		ENCHANTMENTS.register( "magic_protection", ()->Instances.MAGIC_PROTECTION );
		ENCHANTMENTS.register( "vitality", ()->Instances.VITALITY );
		ENCHANTMENTS.register( "immortality", ()->Instances.IMMORTALITY );
		ENCHANTMENTS.register( "absorber", ()->Instances.ABSORBER );
		ENCHANTMENTS.register( "smelter", ()->Instances.SMELTER );
		ENCHANTMENTS.register( "gotta_mine_fast", ()->Instances.GOTTA_MINE_FAST );
		ENCHANTMENTS.register( "telekinesis", ()->Instances.TELEKINESIS );
		ENCHANTMENTS.register( "swiftness", ()->Instances.SWIFTNESS );
		ENCHANTMENTS.register( "horse_protection", ()->Instances.HORSE_PROTECTION );
		ENCHANTMENTS.register( "horse_frost_walker", ()->Instances.HORSE_FROST_WALKER );
		ENCHANTMENTS.register( "hunter", ()->Instances.HUNTER );
		ENCHANTMENTS.register( "elder_guardian_favor", ()->Instances.ELDER_GAURDIAN_FAVOR );
		ENCHANTMENTS.register( "harvester", ()->Instances.HARVESTER );
		addCurses();
		ENCHANTMENTS.register( modEventBus );
	}

	/** Registering all curses. */
	private static void addCurses() {
		ENCHANTMENTS.register( "slowness_curse", ()->Instances.SLOWNESS );
		ENCHANTMENTS.register( "fatigue_curse", ()->Instances.FATIGUE );
		ENCHANTMENTS.register( "incompatibility_curse", ()->Instances.INCOMPATIBILITY );
		ENCHANTMENTS.register( "vampirism_curse", ()->Instances.VAMPIRISM );
		ENCHANTMENTS.register( "corrosion_curse", ()->Instances.CORROSION );
	}

	/** Adds new enchantment types to item groups. (each new enchantment will be
	 automatically added to this group)
	 */
	private static void addEnchantmentTypesToItemGroups() {
		ItemHelper.addEnchantmentTypesToItemGroup( ItemGroup.COMBAT, SHIELD, BOW_AND_CROSSBOW );
		ItemHelper.addEnchantmentTypeToItemGroup( HORSE_ARMOR, ItemGroup.MISC );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.replaceStandardMinecraftHorseArmorLayer();
	}
}
