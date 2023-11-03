package com.majruszsenchantments.common;

import com.majruszsenchantments.MajruszsEnchantments;
import com.mlib.data.Serializable;
import com.mlib.item.CustomEnchantment;
import com.mlib.registry.RegistryObject;

public class Handler {
	protected final RegistryObject< ? extends CustomEnchantment > enchantment;
	protected final Serializable config;
	protected boolean isEnabled = true;

	public < Type extends CustomEnchantment > Handler( RegistryObject< Type > enchantment, boolean isCurse ) {
		this.enchantment = enchantment;
		this.config = new Serializable();
		this.config.defineBoolean( "is_enabled", ()->this.isEnabled, x->{
			this.isEnabled = x;
			this.enchantment.ifPresent( y->y.setEnabled( this.isEnabled ) );
		} );

		Serializable config = isCurse ? MajruszsEnchantments.CONFIG.curses : MajruszsEnchantments.CONFIG.enchantments;
		config.defineCustom( enchantment.getId(), ()->this.config );
	}
}
