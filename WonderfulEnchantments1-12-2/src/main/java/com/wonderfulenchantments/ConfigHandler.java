package com.wonderfulenchantments;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config( modid = WonderfulEnchantments.MOD_ID, name = "wonderful-enchantments", type = Type.INSTANCE, category = "wonderful-enchantments" )
public class ConfigHandler {
	public static final Enchantments enchantments = new Enchantments();
	public static final Curses curses = new Curses();

	public static class Enchantments {
		@Name( "fishing_fanatic" )
		@Comment( "Makes 'Fishing Fanatic' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean FISHING_FANATIC = true;

		@Name( "enchantment_against_humanity" )
		@Comment( "Makes 'Enchantment Against Humanity' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean HUMAN_SLAYER = true;

		@Name( "dodge" )
		@Comment( "Makes 'Dodge' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean DODGE = true;

		@Name( "enlightenment" )
		@Comment( "Makes 'Enlightenment' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean ENLIGHTENMENT = true;

		@Name( "vitality" )
		@Comment( "Makes 'Vitality' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean VITALITY = true;

		@Name( "phoenix_dive" )
		@Comment( "Makes 'Phoenix Dive' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean PHOENIX_DIVE = true;

		@Name( "pufferfish_vengeance" )
		@Comment( "Makes 'Vengeance of Pufferfish' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean PUFFERFISH_VENGEANCE = true;

		@Name( "immortality" )
		@Comment( "Makes 'Immortality' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean IMMORTALITY = true;

		@Name( "smelter" )
		@Comment( "Makes 'Smelter' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean SMELTER = true;
	}

	public static class Curses {
		@Name( "curse_of_slowness" )
		@Comment( "Makes 'Curse of Slowness' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean SLOWNESS = true;

		@Name( "curse_of_fatigue" )
		@Comment( "Makes 'Curse of Fatigue' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean FATIGUE = true;
	}


	/*
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
		Values.SMELTER					= createConfigSpec( "smelter", "Smelter" );
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
			IMMORTALITY,
			SMELTER;

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
	}*/
}
