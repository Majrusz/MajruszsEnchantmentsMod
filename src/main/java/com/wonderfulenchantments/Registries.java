package com.wonderfulenchantments;

import com.mlib.gamemodifiers.GameModifier;
import com.mlib.items.ItemHelper;
import com.mlib.loot_modifiers.AnyModification;
import com.mlib.loot_modifiers.HarvestCrop;
import com.mlib.registries.DeferredRegisterHelper;
import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.items.DyeableHorseArmorItemReplacement;
import com.wonderfulenchantments.items.HorseArmorItemReplacement;
import com.wonderfulenchantments.items.ShieldItemReplacement;
import com.wonderfulenchantments.loot_modifiers.AddItemsDirectlyToInventory;
import com.wonderfulenchantments.loot_modifiers.Replant;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class Registries {
	private static final DeferredRegisterHelper HELPER = new DeferredRegisterHelper( WonderfulEnchantments.MOD_ID );
	private static final DeferredRegisterHelper MINECRAFT_HELPER = new DeferredRegisterHelper( "minecraft" );
	public static final List< GameModifier > GAME_MODIFIERS = new ArrayList<>();

	// Groups
	static final DeferredRegister< Enchantment > ENCHANTMENTS = HELPER.create( ForgeRegistries.Keys.ENCHANTMENTS );
	static final DeferredRegister< Item > ITEMS_TO_REPLACE = MINECRAFT_HELPER.create( ForgeRegistries.Keys.ITEMS );
	static final DeferredRegister< GlobalLootModifierSerializer< ? > > LOOT_MODIFIERS = HELPER.create( ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS );

	// Enchantment Categories
	public static final EnchantmentCategory SHIELD = EnchantmentCategory.create( "shield", item->item instanceof ShieldItem );
	public static final EnchantmentCategory HORSE_ARMOR = EnchantmentCategory.create( "horse_armor", item->item instanceof HorseArmorItem );
	public static final EnchantmentCategory BOW_AND_CROSSBOW = EnchantmentCategory.create( "bow_and_crossbow", item->item instanceof BowItem || item instanceof CrossbowItem );
	public static final EnchantmentCategory MELEE_WEAPON = EnchantmentCategory.create( "melee_weapon", item->item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem );

	// Enchantments

	// Curses
	public static final RegistryObject< FatigueCurse > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse.create() );
	public static final RegistryObject< IncompatibilityCurse > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse.create() );

	// Item Replacements
	static {
		ITEMS_TO_REPLACE.register( "shield", ShieldItemReplacement::new );
		ITEMS_TO_REPLACE.register( "leather_horse_armor", ()->new DyeableHorseArmorItemReplacement( 3, "leather" ) );
		ITEMS_TO_REPLACE.register( "iron_horse_armor", ()->new HorseArmorItemReplacement( 5, "iron" ) );
		ITEMS_TO_REPLACE.register( "golden_horse_armor", ()->new HorseArmorItemReplacement( 7, "gold" ) );
		ITEMS_TO_REPLACE.register( "diamond_horse_armor", ()->new HorseArmorItemReplacement( 11, "diamond" ) );
	}

	// Loot Modifiers
	static {
		LOOT_MODIFIERS.register( "telekinesis_enchantment", AddItemsDirectlyToInventory.Serializer::new );
		LOOT_MODIFIERS.register( "harvester_enchantment", Replant.Serializer::new );
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

		addEnchantmentTypesToItemGroups();
		HELPER.registerAll();
		MINECRAFT_HELPER.registerAll();
		modEventBus.addListener( Registries::doClientSetup );
		modEventBus.addListener( PacketHandler::registerPacket );
		DistExecutor.safeRunWhenOn( Dist.CLIENT, () -> RegistriesClient::createConfig );

		WonderfulEnchantments.CONFIG_HANDLER.register( ModLoadingContext.get() );
		WonderfulEnchantments.CONFIG_HANDLER_CLIENT.register( ModLoadingContext.get() );
	}

	private static void addEnchantmentTypesToItemGroups() {
		ItemHelper.addEnchantmentTypesToItemGroup( CreativeModeTab.TAB_COMBAT, SHIELD, BOW_AND_CROSSBOW, MELEE_WEAPON );
		ItemHelper.addEnchantmentTypeToItemGroup( CreativeModeTab.TAB_MISC, HORSE_ARMOR );
	}

	private static void doClientSetup( final FMLClientSetupEvent event ) {
		RegistriesClient.setup();
	}
}
