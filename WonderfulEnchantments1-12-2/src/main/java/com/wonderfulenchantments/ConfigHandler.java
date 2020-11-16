package com.wonderfulenchantments;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config( modid = WonderfulEnchantments.MOD_ID, name = "wonderful-enchantments", type = Config.Type.INSTANCE )
public class ConfigHandler {
	public static EnchantmentGroup Enchantments = new EnchantmentGroup();
	public static CurseGroup Curses = new CurseGroup();

	public static class EnchantmentGroup {
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

		@Name( "vengeance_of_pufferfish" )
		@Comment( "Makes 'Vengeance of Pufferfish' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean VENGEANCE_OF_PUFFERFISH = true;

		@Name( "immortality" )
		@Comment( "Makes 'Immortality' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean IMMORTALITY = true;
	}

	public static class CurseGroup {
		@Name( "curse_of_slowness" )
		@Comment( "Makes 'Curse of Slowness' obtainable in survival mode." )
		@RequiresMcRestart
		public boolean SLOWNESS = true;
	}
}
