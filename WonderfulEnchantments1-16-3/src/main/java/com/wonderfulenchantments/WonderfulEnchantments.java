package com.wonderfulenchantments;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod( WonderfulEnchantments.MOD_ID )
public class WonderfulEnchantments {
    public WonderfulEnchantments() {
        RegistryHandler.init();

        ConfigHandler.register( ModLoadingContext.get() );

        MinecraftForge.EVENT_BUS.register( this );
    }

    public static final String
        MOD_ID      = "wonderful_enchantments",
        NAME        = "Wonderful Enchantments",
        VERSION     = "0.9.0";

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
}