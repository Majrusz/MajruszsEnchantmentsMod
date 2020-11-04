package com.wonderfulenchantments;

import com.wonderfulenchantments.enchantments.DodgeEnchantment;
import com.wonderfulenchantments.enchantments.FanaticEnchantment;
import com.wonderfulenchantments.enchantments.HumanSlayerEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister< Enchantment > ENCHANTMENTS = new DeferredRegister<>( ForgeRegistries.ENCHANTMENTS, WonderfulEnchantments.MODID );

    public static void init() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENCHANTMENTS.register( modEventBus );
    }

    // Enchantments
    public static final RegistryObject< Enchantment >
        FISHING_FANATIC     = ENCHANTMENTS.register( "fishing_fanatic", FanaticEnchantment::new ),
        HUMAN_SLAYER        = ENCHANTMENTS.register( "human_slayer", HumanSlayerEnchantment::new ),
        DODGE               = ENCHANTMENTS.register( "dodge", DodgeEnchantment::new );


}
