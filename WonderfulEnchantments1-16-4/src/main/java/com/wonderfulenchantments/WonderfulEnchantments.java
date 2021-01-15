package com.wonderfulenchantments;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import com.mlib.config.DoubleConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod( WonderfulEnchantments.MOD_ID )
public class WonderfulEnchantments {
    public static final String MOD_ID = "wonderful_enchantments";
    public static final String NAME = "Wonderful Enchantments";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
    public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "wonderful-enchantments-new.toml" );
    public static final ConfigGroup ENCHANTMENT_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Enchantments", "" ) );
    public static final ConfigGroup CURSE_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Curses", "" ) );

    public WonderfulEnchantments() {
        RegistryHandler.init();

        ConfigHandlerOld.register( ModLoadingContext.get() );

        MinecraftForge.EVENT_BUS.register( this );
    }
}