package com.majruszsenchantments.config;

import com.mlib.data.Serializables;

public class Config extends com.mlib.data.Config {
	public Enchantments enchantments = new Enchantments();
	public Curses curses = new Curses();

	public Config( String name ) {
		super( name );

		Serializables.get( Config.class )
			.defineCustom( "enchantments", ()->this.enchantments )
			.defineCustom( "curses", ()->this.curses );
	}

	public static class Enchantments {}

	public static class Curses {}
}
