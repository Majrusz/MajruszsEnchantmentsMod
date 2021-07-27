package com.wonderfulenchantments;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 Main class for the whole Wonderful Enchantments modification.

 @author Majrusz
 @since 2020-11-03 */
@Mod( WonderfulEnchantments.MOD_ID )
public class WonderfulEnchantments {
	public static final String MOD_ID = "wonderful_enchantments";
	public static final String NAME = "Wonderful Enchantments";
	public static final String VERSION = "1.4.0";
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "wonderful-enchantments-common.toml" );
	public static final ConfigGroup ENCHANTMENT_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Enchantments", "" ) );
	public static final ConfigGroup CURSE_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Curses", "" ) );
	public static final ConfigGroup ITEM_GROUP = CONFIG_HANDLER.addConfigGroup( new ConfigGroup( "Items", "" ) );
	public static final ConfigHandler CONFIG_HANDLER_CLIENT = new ConfigHandler( ModConfig.Type.CLIENT, "wonderful-enchantments-client.toml" );

	public WonderfulEnchantments() {
		RegistryHandler.init();
		MinecraftForge.EVENT_BUS.register( this );
	}

	/** Returns resource location for register in current modification files. */
	public static ResourceLocation getLocation( String register ) {
		return new ResourceLocation( MOD_ID, register );
	}
}