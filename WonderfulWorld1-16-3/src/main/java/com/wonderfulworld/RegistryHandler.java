package com.wonderfulworld;

import com.wonderfulworld.blocks.*;
import com.wonderfulworld.items.*;
import com.wonderfulworld.structures.KremowkaStructure;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
	public static final DeferredRegister< Item > ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, WonderfulWorld.MOD_ID );
	public static final DeferredRegister< Block > BLOCKS = DeferredRegister.create( ForgeRegistries.BLOCKS, WonderfulWorld.MOD_ID );
	public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, WonderfulWorld.MOD_ID );
	public static final DeferredRegister< Feature< ? > > FEATURES = DeferredRegister.create( ForgeRegistries.FEATURES, WonderfulWorld.MOD_ID );
	public static final DeferredRegister< Structure< ? > > STRUCTURES = DeferredRegister.create( ForgeRegistries.STRUCTURE_FEATURES, WonderfulWorld.MOD_ID );

	public static void init() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ITEMS.register( modEventBus );
		BLOCKS.register( modEventBus );
		ENCHANTMENTS.register( modEventBus );
		FEATURES.register( modEventBus );
		STRUCTURES.register( modEventBus );

		VillagerTrades.register();
	}
	
	// Items
	public static final RegistryObject< Item >
			KREMOWKA			= ITEMS.register( "kremowka", KremowkaItem::new ),
			ENCHANTED_KREMOWKA	= ITEMS.register( "enchanted_kremowka", EnchantedKremowkaItem::new ),
			BASS				= ITEMS.register( "bass", BassItem::new ),
			COOKED_BASS			= ITEMS.register( "cooked_bass", CookedBassItem::new ),
			HERRING				= ITEMS.register( "herring", HerringItem::new ),
			SURSTROMMING		= ITEMS.register( "surstromming", SurstrommingItem::new ),
			SALT				= ITEMS.register( "salt", SaltItem::new ),
			CAKE_SLICE			= ITEMS.register( "cake_slice", CakeSliceItem::new ),
			TIN					= ITEMS.register( "tin", TinItem::new );
	
	// Blocks
	public static final RegistryObject< Block >
			KREMOWKA_BLOCK 	= BLOCKS.register( "kremowka_block", KremowkaBlock::new ),
			SALT_ORE		= BLOCKS.register( "salt_ore", SaltOre::new ),
			NETHER_SALT_ORE	= BLOCKS.register( "nether_salt_ore", NetherSaltOre::new ),
			NETHER_IRON_ORE	= BLOCKS.register( "nether_iron_ore", NetherIronOre::new );

	// Block items
	public static final RegistryObject< Item >
			KREMOWKA_BLOCK_ITEM 	= ITEMS.register( "kremowka_block", () -> new BlockItemBase( KREMOWKA_BLOCK.get() ) ),
			SALT_ORE_ITEM 			= ITEMS.register( "salt_ore", () -> new BlockItemBase( SALT_ORE.get() ) ),
			NETHER_SALT_ORE_ITEM 	= ITEMS.register( "nether_salt_ore", () -> new BlockItemBase( NETHER_SALT_ORE.get() ) ),
			NETHER_IRON_ORE_ITEM 	= ITEMS.register( "nether_iron_ore", () -> new BlockItemBase( NETHER_IRON_ORE.get() ) );

	// Structures
	public static final RegistryObject< Structure< ? > >
		KREMOWKA_STRUCTURE = STRUCTURES.register( "kremowka_structure", KremowkaStructure::new );
}
