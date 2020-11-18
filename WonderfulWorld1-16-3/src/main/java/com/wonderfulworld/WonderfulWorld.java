package com.wonderfulworld;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod( WonderfulWorld.MOD_ID )
public class WonderfulWorld {
    public WonderfulWorld() {
        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register( this );
    }

    public static final ItemGroup TAB_ITEMS = new ItemGroup( "wonderful_world_tab_items" ) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack( RegistryHandler.KREMOWKA.get() );
        }
    };

    public static final ItemGroup TAB_BLOCKS = new ItemGroup( "wonderful_world_tab_blocks" ) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack( RegistryHandler.KREMOWKA_BLOCK_ITEM.get() );
        }
    };

    public static final String
            MOD_ID      = "wonderful_world",
            NAME        = "Wonderful World",
            VERSION     = "0.6.0";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();
}