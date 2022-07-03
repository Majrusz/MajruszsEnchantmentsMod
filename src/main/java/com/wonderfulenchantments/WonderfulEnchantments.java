package com.wonderfulenchantments;

import com.mlib.config.ConfigGroup;
import com.mlib.config.ConfigHandler;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
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
	public static final String MOD_ID = "wonderfulenchantments";
	public static final String NAME = "Wonderful Enchantments";
	public static final ConfigHandler CONFIG_HANDLER = new ConfigHandler( ModConfig.Type.COMMON, "common.toml", MOD_ID );
	public static final ConfigGroup ENCHANTMENTS_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( EnchantmentModifier.ENCHANTMENT, "Enchantments", "" );
	public static final ConfigGroup CURSES_GROUP = CONFIG_HANDLER.addNewGameModifierGroup( EnchantmentModifier.CURSE, "Curses", "" );
	public static final ConfigHandler CONFIG_HANDLER_CLIENT = new ConfigHandler( ModConfig.Type.CLIENT, "client.toml", MOD_ID );

	public WonderfulEnchantments() {
		Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}