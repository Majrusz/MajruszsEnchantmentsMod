package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.enchantments.*;
import com.wonderfulenchantments.items.DyeableHorseArmorItemReplacement;
import com.wonderfulenchantments.items.HorseArmorItemReplacement;
import com.wonderfulenchantments.items.ShieldItemReplacement;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< ParticleType< ? > > PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< Item > ITEMS_TO_REPLACE = DeferredRegister.create( ForgeRegistries.ITEMS, "minecraft" );

    // Fishing Rod Enchantments
    public static final RegistryObject< Enchantment > FISHING_FANATIC = ENCHANTMENTS.register( "fishing_fanatic", FanaticEnchantment::new );

    // Sword Enchantments
    public static final RegistryObject< Enchantment > HUMAN_SLAYER = ENCHANTMENTS.register( "human_slayer", HumanSlayerEnchantment::new );
    public static final RegistryObject< Enchantment > PUFFERFISH_VENGEANCE = ENCHANTMENTS.register( "pufferfish_vengeance", PufferfishVengeanceEnchantment::new );
    public static final RegistryObject< Enchantment > LEECH = ENCHANTMENTS.register( "leech", LeechEnchantment::new );

    // Armor Enchantments
    public static final RegistryObject< Enchantment > DODGE = ENCHANTMENTS.register( "dodge", DodgeEnchantment::new );
    public static final RegistryObject< Enchantment > ENLIGHTENMENT = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment::new );
    public static final RegistryObject< Enchantment > PHOENIX_DIVE = ENCHANTMENTS.register( "phoenix_dive", PhoenixDiveEnchantment::new );
    public static final RegistryObject< Enchantment > MAGIC_PROTECTION = ENCHANTMENTS.register( "magic_protection", MagicProtectionEnchantment::new );

    // Shield Enchantments
    public static final RegistryObject< Enchantment > VITALITY = ENCHANTMENTS.register( "vitality", VitalityEnchantment::new );
    public static final RegistryObject< Enchantment > IMMORTALITY = ENCHANTMENTS.register( "immortality", ImmortalityEnchantment::new );

    // Tool Enchantments
    public static final RegistryObject< Enchantment > SMELTER = ENCHANTMENTS.register( "smelter", SmelterEnchantment::new );
    public static final RegistryObject< Enchantment > GOTTA_MINE_FAST = ENCHANTMENTS.register( "gotta_mine_fast", GottaMineFastEnchantment::new );
    public static final RegistryObject< Enchantment > TELEKINESIS = ENCHANTMENTS.register( "telekinesis", TelekinesisEnchantment::new );

    // Horse Armor Enchantments
    public static final RegistryObject< Enchantment > SWIFTNESS = ENCHANTMENTS.register( "swiftness", SwiftnessEnchantment::new );
    public static final RegistryObject< Enchantment > HORSE_PROTECTION = ENCHANTMENTS.register( "horse_protection", HorseProtectionEnchantment::new );
    public static final RegistryObject< Enchantment > HORSE_FROST_WALKER = ENCHANTMENTS.register( "horse_frost_walker", HorseFrostWalkerEnchantment::new );

    // Curses
    public static final RegistryObject< Enchantment > SLOWNESS = ENCHANTMENTS.register( "slowness_curse", SlownessCurse::new );
    public static final RegistryObject< Enchantment > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", FatigueCurse::new );
    public static final RegistryObject< Enchantment > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", IncompatibilityCurse::new );

    // Particles
    public static final RegistryObject< BasicParticleType > PHOENIX_PARTICLE = PARTICLES.register( "phoenix_particle", ()->new BasicParticleType( true ) );

    public static void init() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();

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

    private static void registerObjects( final IEventBus modEventBus ) {
        ENCHANTMENTS.register( modEventBus );
        PARTICLES.register( modEventBus );
        ITEMS_TO_REPLACE.register( modEventBus );
    }

    private static void addEnchantmentTypesToItemGroups() {
        WonderfulEnchantmentHelper.addTypeToItemGroup( WonderfulEnchantmentHelper.SHIELD, ItemGroup.COMBAT );
        WonderfulEnchantmentHelper.addTypeToItemGroup( WonderfulEnchantmentHelper.HORSE_ARMOR, ItemGroup.MISC );
    }

    private static void doClientSetup( final FMLClientSetupEvent event ) {
        RegistryHandlerClient.replaceStandardMinecraftHorseArmorLayer();
    }
}
