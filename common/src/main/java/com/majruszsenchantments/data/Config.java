package com.majruszsenchantments.data;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.Serializables;

public class Config {
	public static boolean IS_SHIELD_ENCHANTABLE = true;
	public static boolean IS_HORSE_ARMOR_ENCHANTABLE = true;

	static {
		Serializables.getStatic( Config.class )
			.define( "is_shield_enchantable", Reader.bool(), ()->IS_SHIELD_ENCHANTABLE, v->IS_SHIELD_ENCHANTABLE = v )
			.define( "is_horse_armor_enchantable", Reader.bool(), ()->IS_HORSE_ARMOR_ENCHANTABLE, v->IS_HORSE_ARMOR_ENCHANTABLE = v )
			.define( "enchantments", Enchantments.class )
			.define( "curses", Curses.class );

		Serializables.getStatic( Enchantments.class );

		Serializables.getStatic( Curses.class );
	}

	public static class Enchantments {}

	public static class Curses {}
}
