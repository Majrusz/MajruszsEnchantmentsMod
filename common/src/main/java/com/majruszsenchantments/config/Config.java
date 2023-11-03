package com.majruszsenchantments.config;

import com.mlib.data.Serializable;

public class Config extends com.mlib.data.Config {
	public Enchantments enchantments = new Enchantments();
	public Curses curses = new Curses();

	public Config( String name ) {
		super( name );

		this.defineCustom( "enchantments", ()->this.enchantments );
		this.defineCustom( "curses", ()->this.curses );
	}

	public static class Enchantments extends Serializable {}

	public static class Curses extends Serializable {}
}
