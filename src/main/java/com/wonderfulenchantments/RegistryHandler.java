package com.wonderfulenchantments;

import com.mlib.items.ItemHelper;
import com.wonderfulenchantments.curses.WonderfulCurse;
import com.wonderfulenchantments.enchantments.WonderfulEnchantment;
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

/** Class responsible for registering all objects. (items, enchantments etc.) */
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
	public static final EnchantmentType BOW_AND_CROSSBOW = EnchantmentType.create( "bow_and_crossbow",
		( Item item )->( item instanceof BowItem || item instanceof CrossbowItem )
	);

	public static final RegistryObject< BasicParticleType > PHOENIX_PARTICLE = PARTICLES.register( "phoenix_particle",
		()->new BasicParticleType( true )
	);

	/** General initialization of all objects. */
	public static void init() {
		FMLJavaModLoadingContext modLoadingContext = FMLJavaModLoadingContext.get();
		final IEventBus modEventBus = modLoadingContext.getModEventBus();

		new Instances(); // required because otherwise enchantments may not load properly before registering them
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
		for( WonderfulEnchantment enchantment : WonderfulEnchantment.ENCHANTMENT_LIST )
			enchantment.register( ENCHANTMENTS );

		for( WonderfulCurse curse : WonderfulCurse.CURSE_LIST )
			curse.register( ENCHANTMENTS );

		ENCHANTMENTS.register( modEventBus );
	}

	/**
	 Adds new enchantment types to item groups. (each new enchantment will be
	 automatically added to this group)
	 */
	private static void addEnchantmentTypesToItemGroups() {
		ItemHelper.addEnchantmentTypesToItemGroup( ItemGroup.COMBAT, SHIELD, BOW_AND_CROSSBOW );
		ItemHelper.addEnchantmentTypeToItemGroup( HORSE_ARMOR, ItemGroup.MISC );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistryHandlerClient.setup();
	}
}
