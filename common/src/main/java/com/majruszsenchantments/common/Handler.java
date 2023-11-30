package com.majruszsenchantments.common;

import com.majruszlibrary.data.Reader;
import com.majruszlibrary.data.SerializableObject;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.registry.RegistryObject;
import com.majruszsenchantments.data.Config;

public class Handler {
	protected final RegistryObject< ? extends CustomEnchantment > enchantment;
	protected final SerializableObject< ? > config;
	protected boolean isEnabled = true;

	public < Type extends CustomEnchantment > Handler( RegistryObject< Type > enchantment, Class< ? extends Handler > clazz, boolean isCurse ) {
		this.enchantment = enchantment;
		this.config = Serializables.getStatic( clazz );
		this.config.define( "is_enabled", Reader.bool(), s->this.isEnabled, ( s, v )->{
			this.isEnabled = v;
			this.enchantment.ifPresent( y->y.setEnabled( this.isEnabled ) );
		} );

		Class< ? > parent = isCurse ? Config.Curses.class : Config.Enchantments.class;
		Serializables.getStatic( parent )
			.define( enchantment.getId(), clazz );
	}
}
