package com.wonderfulenchantments;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;

	private static void load() {
		BUILDER.comment( "Remember to restart your client/server after changing config!" );

		BUILDER.push( "Enchantments" );
		Values.FISHING_FANATIC			= createConfigSpec( "fishing_fanatic", "Fishing Fanatic" );
		Values.HUMAN_SLAYER				= createConfigSpec( "human_slayer", "Enchantment Against Humanity" );
		Values.DODGE					= createConfigSpec( "dodge", "Dodge" );
		Values.ENLIGHTENMENT			= createConfigSpec( "enlightenment", "Enlightenment" );
		Values.VITALITY					= createConfigSpec( "vitality", "Vitality" );
		Values.PHOENIX_DIVE				= createConfigSpec( "phoenix_dive", "Phoenix Dive" );
		Values.PUFFERFISH_VENGEANCE		= createConfigSpec( "pufferfish_vengeance", "Vengeance of Pufferfish" );
		Values.IMMORTALITY				= createConfigSpec( "immortality", "Immortality" );
		BUILDER.pop();

		BUILDER.push( "Curses" );
		Values.SLOWNESS					= createConfigSpec( "slowness", "Curse of Slowness" );
		Values.FATIGUE					= createConfigSpec( "fatigue", "Curse of Fatigue" );
		BUILDER.pop();

		CONFIG = BUILDER.build();
	}

	public static void register( final ModLoadingContext context ) {
		ConfigHandler.load();

		context.registerConfig( ModConfig.Type.COMMON, ConfigHandler.CONFIG, "wonderful-enchantments.toml" );
	}

	public static class Values {
		// Enchantments
		public static ForgeConfigSpec.BooleanValue
			FISHING_FANATIC,
			HUMAN_SLAYER,
			DODGE,
			ENLIGHTENMENT,
			VITALITY,
			PHOENIX_DIVE,
			PUFFERFISH_VENGEANCE,
			IMMORTALITY;

		// Curses
		public static ForgeConfigSpec.BooleanValue
			SLOWNESS,
			FATIGUE;
	}

	private static ForgeConfigSpec.BooleanValue createConfigSpec( String enchantmentLanguageName, String enchantmentName ) {
		return BUILDER
			.comment( String.format( "Makes '%s' obtainable in survival mode.", enchantmentName ) )
			.worldRestart()
			.define( enchantmentLanguageName, true );
	}
}
