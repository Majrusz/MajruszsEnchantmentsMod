package com.majruszsenchantments.common;

import com.majruszsenchantments.config.Config;
import com.mlib.data.Serializable;
import com.mlib.data.Serializables;
import com.mlib.item.CustomEnchantment;
import com.mlib.registry.RegistryObject;

public class Handler {
	protected final RegistryObject< ? extends CustomEnchantment > enchantment;
	protected final Serializable< ? > config;
	protected boolean isEnabled = true;

	public < Type extends CustomEnchantment > Handler( RegistryObject< Type > enchantment, boolean isCurse ) {
		this.enchantment = enchantment;
		this.config = new Serializable<>();
		this.config.defineBoolean( "is_enabled", s->this.isEnabled, ( s, v )->{
			this.isEnabled = v;
			this.enchantment.ifPresent( y->y.setEnabled( this.isEnabled ) );
		} );

		Class< ? > clazz = isCurse ? Config.Curses.class : Config.Enchantments.class;
		Serializables.get( clazz )
			.defineCustom( enchantment.getId(), ()->this.config );
	}
}
