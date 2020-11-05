package com.wonderfulenchantments;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod( "wonderful_enchantments" )
public class WonderfulEnchantments {
    public WonderfulEnchantments() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::setup );
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::doClientStuff );

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register( this );
    }

    private void setup( final FMLCommonSetupEvent event ) {

    }

    private void doClientStuff( final FMLClientSetupEvent event ) {

    }

    public static final String MODID = "wonderful_enchantments", VERSION = "0.2.0";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
}