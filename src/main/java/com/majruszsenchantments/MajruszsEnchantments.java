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
	public static final ConfigHandler SERVER_CONFIG = new ConfigHandler( ModConfig.Type.SERVER );
	public static final ConfigHandler CLIENT_CONFIG = new ConfigHandler( ModConfig.Type.CLIENT );

	public MajruszsEnchantments() {
		com.majruszsenchantments.Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}