package com.majruszsenchantments.data;

import com.majruszlibrary.data.Serializables;

public class Config {
	static {
		Serializables.getStatic( Config.class )
			.define( "enchantments", Enchantments.class )
			.define( "curses", Curses.class );

		Serializables.getStatic( Enchantments.class );

		Serializables.getStatic( Curses.class );
	}

	public static class Enchantments {}

	public static class Curses {}
}
