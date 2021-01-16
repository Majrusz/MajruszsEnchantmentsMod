package com.wonderfulenchantments;

import com.mlib.MajruszLibrary;
import com.mlib.items.ItemHelper;
import com.wonderfulenchantments.curses.*;
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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< ParticleType< ? > > PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< Item > ITEMS_TO_REPLACE = DeferredRegister.create( ForgeRegistries.ITEMS, "minecraft" );

    // Fishing Rod Enchantments
    public static final RegistryObject< Enchantment > FISHING_FANATIC = ENCHANTMENTS.register( "fishing_fanatic", ()->Instances.FISHING_FANATIC );

    // Sword Enchantments
    public static final RegistryObject< Enchantment > HUMAN_SLAYER = ENCHANTMENTS.register( "human_slayer", ()->Instances.HUMAN_SLAYER );
    public static final RegistryObject< Enchantment > PUFFERFISH_VENGEANCE = ENCHANTMENTS.register( "pufferfish_vengeance", ()->Instances.PUFFERFISH_VENGEANCE );
    public static final RegistryObject< Enchantment > LEECH = ENCHANTMENTS.register( "leech", ()->Instances.LEECH );

    // Armor Enchantments
    public static final RegistryObject< Enchantment > DODGE = ENCHANTMENTS.register( "dodge", ()->Instances.DODGE );
    public static final RegistryObject< Enchantment > ENLIGHTENMENT = ENCHANTMENTS.register( "enlightenment", ()->Instances.ENLIGHTENMENT );
    public static final RegistryObject< Enchantment > PHOENIX_DIVE = ENCHANTMENTS.register( "phoenix_dive", ()->Instances.PHOENIX_DIVE );
    public static final RegistryObject< Enchantment > MAGIC_PROTECTION = ENCHANTMENTS.register( "magic_protection", ()->Instances.MAGIC_PROTECTION );

    // Shield Enchantments
    public static final RegistryObject< Enchantment > VITALITY = ENCHANTMENTS.register( "vitality", ()->Instances.VITALITY );
    public static final RegistryObject< Enchantment > IMMORTALITY = ENCHANTMENTS.register( "immortality", ()->Instances.IMMORTALITY );
    public static final RegistryObject< Enchantment > ABSORBER = ENCHANTMENTS.register( "absorber", ()->Instances.ABSORBER );

    // Tool Enchantments
    public static final RegistryObject< Enchantment > SMELTER = ENCHANTMENTS.register( "smelter", ()->Instances.SMELTER );
    public static final RegistryObject< Enchantment > GOTTA_MINE_FAST = ENCHANTMENTS.register( "gotta_mine_fast", ()->Instances.GOTTA_MINE_FAST );
    public static final RegistryObject< Enchantment > TELEKINESIS = ENCHANTMENTS.register( "telekinesis", ()->Instances.TELEKINESIS );

    // Horse Armor Enchantments
    public static final RegistryObject< Enchantment > SWIFTNESS = ENCHANTMENTS.register( "swiftness", ()->Instances.SWIFTNESS );
    public static final RegistryObject< Enchantment > HORSE_PROTECTION = ENCHANTMENTS.register( "horse_protection", ()->Instances.HORSE_PROTECTION );
    public static final RegistryObject< Enchantment > HORSE_FROST_WALKER = ENCHANTMENTS.register( "horse_frost_walker", ()->Instances.HORSE_FROST_WALKER );

    // Bow Enchantments
    public static final RegistryObject< Enchantment > HUNTER = ENCHANTMENTS.register( "hunter", ()->Instances.HUNTER );

    // Trident Enchantments
    public static final RegistryObject< Enchantment > ELDER_GUARDIAN_FAVOR = ENCHANTMENTS.register( "elder_guardian_favor", ()->Instances.ELDER_GAURDIAN_FAVOR );

    // Curses
    public static final RegistryObject< Enchantment > SLOWNESS = ENCHANTMENTS.register( "slowness_curse", ()->Instances.SLOWNESS );
    public static final RegistryObject< Enchantment > FATIGUE = ENCHANTMENTS.register( "fatigue_curse", ()->Instances.FATIGUE );
    public static final RegistryObject< Enchantment > INCOMPATIBILITY = ENCHANTMENTS.register( "incompatibility_curse", ()->Instances.INCOMPATIBILITY );
    public static final RegistryObject< Enchantment > VAMPIRISM = ENCHANTMENTS.register( "vampirism_curse", ()->Instances.VAMPIRISM );
    public static final RegistryObject< Enchantment > CORROSION = ENCHANTMENTS.register( "corrosion_curse", ()->Instances.CORROSION );

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
        ItemHelper.addEnchantmentTypeToItemGroup( WonderfulEnchantmentHelper.SHIELD, ItemGroup.COMBAT );
        ItemHelper.addEnchantmentTypeToItemGroup( WonderfulEnchantmentHelper.HORSE_ARMOR, ItemGroup.MISC );
    }

    private static void doClientSetup( final FMLClientSetupEvent event ) {
        RegistryHandlerClient.replaceStandardMinecraftHorseArmorLayer();
    }
}
