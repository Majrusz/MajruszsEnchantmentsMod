package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.enchantments.*;
import com.wonderfulenchantments.items.ShieldReplacementItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< ParticleType< ? > > PARTICLES = DeferredRegister.create( ForgeRegistries.PARTICLE_TYPES, WonderfulEnchantments.MOD_ID );
    public static final DeferredRegister< Item > ITEMS_TO_REPLACE = DeferredRegister.create( ForgeRegistries.ITEMS, "minecraft" );

    public static void init() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENCHANTMENTS.register( modEventBus );
        PARTICLES.register( modEventBus );
        ITEMS_TO_REPLACE.register( modEventBus );

        EnchantmentTypes.addTypeToItemGroup( EnchantmentTypes.SHIELD, ItemGroup.COMBAT );
    }

    // Enchantments
    public static final RegistryObject< Enchantment >
            FISHING_FANATIC         = ENCHANTMENTS.register( "fishing_fanatic", FanaticEnchantment::new ),
            HUMAN_SLAYER            = ENCHANTMENTS.register( "human_slayer", HumanSlayerEnchantment::new ),
            DODGE                   = ENCHANTMENTS.register( "dodge", DodgeEnchantment::new ),
            ENLIGHTENMENT           = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment::new ),
            VITALITY                = ENCHANTMENTS.register( "vitality", VitalityEnchantment::new ),
            PHOENIX_DIVE            = ENCHANTMENTS.register( "phoenix_dive", PhoenixDiveEnchantment::new ),
            PUFFERFISH_VENGEANCE    = ENCHANTMENTS.register( "pufferfish_vengeance", PufferfishVengeanceEnchantment::new ),
            IMMORTALITY             = ENCHANTMENTS.register( "immortality", ImmortalityEnchantment::new );

    // Curses
    public static final RegistryObject< Enchantment >
            SLOWNESS                = ENCHANTMENTS.register( "slowness_curse", SlownessCurse::new );

    // Particles
    public static final RegistryObject< BasicParticleType >
            PHOENIX_PARTICLE    = PARTICLES.register( "phoenix_particle", () -> new BasicParticleType( true ) );

    // Items to replace
    public static final RegistryObject< Item >
            SHIELD_REPLACEMENT  = ITEMS_TO_REPLACE.register( "shield", ShieldReplacementItem::new );

    public static final int disableEnchantmentValue = 9001;
}
