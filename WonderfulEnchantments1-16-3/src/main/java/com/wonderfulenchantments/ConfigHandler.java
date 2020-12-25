package com.wonderfulenchantments;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG_SPEC;

	private static void load() {
		BUILDER.comment( "Remember to restart your client/server after changing config!" );

		BUILDER.push( "Enchantments" );
		Config.Enchantability.FISHING_FANATIC = createConfigSpecForEnchantability( "fishing_fanatic", "Fishing Fanatic" );
		Config.Enchantability.HUMAN_SLAYER = createConfigSpecForEnchantability( "human_slayer", "Enchantment Against Humanity" );
		Config.Enchantability.DODGE = createConfigSpecForEnchantability( "dodge", "Dodge" );
		Config.Enchantability.ENLIGHTENMENT = createConfigSpecForEnchantability( "enlightenment", "Enlightenment" );
		Config.Enchantability.VITALITY = createConfigSpecForEnchantability( "vitality", "Vitality" );
		Config.Enchantability.PHOENIX_DIVE = createConfigSpecForEnchantability( "phoenix_dive", "Phoenix Dive" );
		Config.Enchantability.PUFFERFISH_VENGEANCE = createConfigSpecForEnchantability( "pufferfish_vengeance", "Vengeance of Pufferfish" );
		Config.Enchantability.IMMORTALITY = createConfigSpecForEnchantability( "immortality", "Immortality" );
		Config.Enchantability.SMELTER = createConfigSpecForEnchantability( "smelter", "Smelter" );
		Config.Enchantability.GOTTA_MINE_FAST = createConfigSpecForEnchantability( "gotta_mine_fast", "Gotta Mine Fast" );
		Config.Enchantability.LEECH = createConfigSpecForEnchantability( "leech", "Leech" );
		Config.Enchantability.MAGIC_PROTECTION = createConfigSpecForEnchantability( "magic_protection", "Magic Protection" );
		Config.Enchantability.SWIFTNESS = createConfigSpecForEnchantability( "swiftness", "Swiftness" );
		Config.Enchantability.HORSE_PROTECTION = createConfigSpecForEnchantability( "horse_protection", "Horse Protection" );
		BUILDER.pop();

		BUILDER.push( "Curses" );
		Config.Enchantability.SLOWNESS = createConfigSpecForEnchantability( "slowness", "Curse of Slowness" );
		Config.Enchantability.FATIGUE = createConfigSpecForEnchantability( "fatigue", "Curse of Fatigue" );
		BUILDER.pop();

		BUILDER.push( "Bonuses" );
		Config.HUMANITY_DAMAGE_BONUS = createConfigSpecForDouble( "Damage bonus per enchantment level. (Enchantment Against Humanity)", "humanity_damage_bonus", 2.5, 1.0, 10.0 );
		Config.VITALITY_BONUS = createConfigSpecForInteger( "Health bonus per enchantment level. (Vitality)", "health_bonus", 2, 1, 10 );
		Config.MAGIC_PROTECTION_BONUS = createConfigSpecForInteger( "Damage reduction bonus per enchantment level. (Magic Protection)", "magic_protection_bonus", 2, 1, 10 );
		Config.HORSE_ARMOR_BONUS = createConfigSpecForInteger( "Horse armor bonus per enchantment level. (Horse Protection)", "horse_armor_bonus", 2, 1, 10 );
		BUILDER.pop();

		BUILDER.push( "Chances" );
		Config.FISHING_EXTRA_DROP_CHANCE = createConfigSpecForDouble( "Chance for extra fishing drop. (Fishing Fanatic)", "fishing_extra_loot_chance", 0.33334, 0.01, 1.0 );
		Config.DODGE_CHANCE = createConfigSpecForDouble( "Chance to completely avoid damage per enchantment level. (Dodge)", "dodge_chance", 0.125, 0.01, 0.25 );
		Config.LEECH_CHANCE = createConfigSpecForDouble( "Chance for stealing positive effect/health from enemy. (Leech)", "leech_chance", 0.25, 0.01, 1.0 );
		BUILDER.pop();

		BUILDER.push( "Multipliers" );
		Config.EXPERIENCE_MULTIPLIER = createConfigSpecForDouble( "Maximum experience bonus per enchantment level. (Enlightenment)", "enlightenment_experience_multiplier", 0.25, 0.01, 1.0 );
		Config.PHOENIX_JUMP_MULTIPLIER = createConfigSpecForDouble( "Jumping power multiplier per enchantment level. (Phoenix Dive)", "phoenix_jump_multiplier", 0.25, 0.01, 1.0 );
		Config.SWIFTNESS_MULTIPLIER = createConfigSpecForDouble( "Horse movement speed multiplier per enchantment level. (Swiftness)", "swiftness_multiplier", 0.125, 0.01, 0.5 );
		Config.SLOWNESS_MULTIPLIER = createConfigSpecForDouble( "Cumulative movement speed reduction with each enchantment. (Curse of Slowness)", "slowness_multiplier", 0.125, 0.01, 0.15 );
		Config.FATIGUE_MULTIPLIER = createConfigSpecForDouble( "Mining speed reduction from the previous enchantment level. (Curse of Fatigue)", "fatigue_multiplier", 0.7, 0.1, 0.9 );
		BUILDER.pop();

		BUILDER.push( "Durations" );
		Config.PUFFERFISH_DURATION = createConfigSpecForDouble( "Pufferfish debuffs duration (in seconds) per enchantment level. (Vengeance of Pufferfish)", "pufferfish_duration", 2.0, 1.0, 10 );
		Config.VAMPIRISM_DURATION = createConfigSpecForInteger( "Vampirism debuffs duration (in seconds) per enchantment level. (Curse of Vampirism)", "vampirism_duration", 30, 10, 300 );
		BUILDER.pop();

		CONFIG_SPEC = BUILDER.build();
	}

	public static void register( final ModLoadingContext context ) {
		ConfigHandler.load();

		context.registerConfig( ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC, "wonderful-enchantments.toml" );
	}

	public static class Config {
		public static class Enchantability {
			// Enchantments
			public static ForgeConfigSpec.BooleanValue FISHING_FANATIC, HUMAN_SLAYER, DODGE, ENLIGHTENMENT, VITALITY, PHOENIX_DIVE, PUFFERFISH_VENGEANCE, IMMORTALITY, SMELTER, GOTTA_MINE_FAST, LEECH, MAGIC_PROTECTION, SWIFTNESS, HORSE_PROTECTION;

			// Curses
			public static ForgeConfigSpec.BooleanValue SLOWNESS, FATIGUE;
		}

		// Bonuses
		public static ForgeConfigSpec.DoubleValue HUMANITY_DAMAGE_BONUS;
		public static ForgeConfigSpec.IntValue VITALITY_BONUS;
		public static ForgeConfigSpec.IntValue MAGIC_PROTECTION_BONUS;
		public static ForgeConfigSpec.IntValue HORSE_ARMOR_BONUS;

		// Chances
		public static ForgeConfigSpec.DoubleValue FISHING_EXTRA_DROP_CHANCE;
		public static ForgeConfigSpec.DoubleValue DODGE_CHANCE;
		public static ForgeConfigSpec.DoubleValue LEECH_CHANCE;

		// Multipliers
		public static ForgeConfigSpec.DoubleValue EXPERIENCE_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue PHOENIX_JUMP_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue SWIFTNESS_MULTIPLIER;

		// Multipliers (Curses)
		public static ForgeConfigSpec.DoubleValue SLOWNESS_MULTIPLIER;
		public static ForgeConfigSpec.DoubleValue FATIGUE_MULTIPLIER;

		// Durations
		public static ForgeConfigSpec.DoubleValue PUFFERFISH_DURATION;
		public static ForgeConfigSpec.IntValue VAMPIRISM_DURATION;
	}

	private static ForgeConfigSpec.IntValue createConfigSpecForInteger( String comment, String name, int defaultValue, int min, int max ) {
		return BUILDER.comment( comment ).worldRestart().defineInRange( name, defaultValue, min, max );
	}

	private static ForgeConfigSpec.DoubleValue createConfigSpecForDouble( String comment, String name, double defaultValue, double min, double max ) {
		return BUILDER.comment( comment ).worldRestart().defineInRange( name, defaultValue, min, max );
	}

	private static ForgeConfigSpec.BooleanValue createConfigSpecForEnchantability( String enchantmentLanguageName, String enchantmentName ) {
		return BUILDER.comment( String.format( "Makes '%s' obtainable in survival mode.", enchantmentName ) )
			.worldRestart()
			.define( enchantmentLanguageName, true );
	}
}
