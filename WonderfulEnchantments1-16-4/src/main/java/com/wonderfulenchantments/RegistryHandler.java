package com.wonderfulenchantments;

import com.mlib.items.ItemHelper;
import com.wonderfulenchantments.items.DyeableHorseArmorItemReplacement;
import com.wonderfulenchantments.items.HorseArmorItemReplacement;
import com.wonderfulenchantments.items.ShieldItemReplacement;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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

	public static final RegistryObject< BasicParticleType > PHOENIX_PARTICLE = PARTICLES.register( "phoenix_particle",
		()->new BasicParticleType( true )
	);

	public static void init() {
		FMLJavaModLoadingContext modLoadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = modLoadingContext.getModEventBus();

		addEnchantments();
		addCurses();
		replaceRestStandardMinecraftItems();
		registerObjects( modEventBus );
		addEnchantmentTypesToItemGroups();
		modEventBus.addListener( RegistryHandler::doClientSetup );
	}

	// replacing standard minecraft shield and horse armors with the new ones which could be enchanted
	private static void replaceRestStandardMinecraftItems() {
		ITEMS_TO_REPLACE.register( "shield", ShieldItemReplacement::new );
		ITEMS_TO_REPLACE.register( "leather_horse_armor", ()->new DyeableHorseArmorItemReplacement( 3, "leather" ) );
		ITEMS_TO_REPLACE.register( "iron_horse_armor", ()->new HorseArmorItemReplacement( 5, "iron" ) );
		ITEMS_TO_REPLACE.register( "golden_horse_armor", ()->new HorseArmorItemReplacement( 7, "gold" ) );
		ITEMS_TO_REPLACE.register( "diamond_horse_armor", ()->new HorseArmorItemReplacement( 11, "diamond" ) );
	}

	private static void addEnchantments() {
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
	}

	private static void addCurses() {
		ENCHANTMENTS.register( "slowness_curse", ()->Instances.SLOWNESS );
		ENCHANTMENTS.register( "fatigue_curse", ()->Instances.FATIGUE );
		ENCHANTMENTS.register( "incompatibility_curse", ()->Instances.INCOMPATIBILITY );
		ENCHANTMENTS.register( "vampirism_curse", ()->Instances.VAMPIRISM );
		ENCHANTMENTS.register( "corrosion_curse", ()->Instances.CORROSION );
	}

	private static void registerObjects( final IEventBus modEventBus ) {
		ENCHANTMENTS.register( modEventBus );
		PARTICLES.register( modEventBus );
		ITEMS_TO_REPLACE.register( modEventBus );
	}

	private static void addEnchantmentTypesToItemGroups() {
		ItemHelper.addEnchantmentTypeToItemGroup( SHIELD, ItemGroup.COMBAT );
		ItemHelper.addEnchantmentTypeToItemGroup( HORSE_ARMOR, ItemGroup.MISC );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.replaceStandardMinecraftHorseArmorLayer();
	}
}
