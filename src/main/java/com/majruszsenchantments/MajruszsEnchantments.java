package com.majruszsenchantments;

import com.mlib.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 Main class for the whole Wonderful Enchantments modification.

 @author Majrusz
 @since 2020-11-03 */
@Mod( MajruszsEnchantments.MOD_ID )
public class MajruszsEnchantments {
	public static final String MOD_ID = "majruszsenchantments";
	public static final String NAME = "Majrusz's Enchantments";
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "common.toml", MOD_ID );
	public static final ConfigHandler CONFIG_HANDLER_CLIENT = new ConfigHandler( ModConfig.Type.CLIENT, "client.toml", MOD_ID );

	public MajruszsEnchantments() {
		Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}