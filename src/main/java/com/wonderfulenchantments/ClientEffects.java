package com.wonderfulenchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;

/** All client-side effects manager. */
public class ClientEffects {
	protected final ConfigGroup effectsGroup;
	protected final AvailabilityConfig enchantmentBookReplacement;

	public ClientEffects() {
		String book_comment = "Should the Enchanted Book have a different texture when it has any of the Wonderful Enchantments on it? (disabling it may fix some bugs with other mods)";
		this.enchantmentBookReplacement = new AvailabilityConfig( "enchantment_book_replacement", book_comment, true, true );

		this.effectsGroup = new ConfigGroup( "Effects", "" );
		this.effectsGroup.addConfig( this.enchantmentBookReplacement );

		WonderfulEnchantments.CONFIG_HANDLER_CLIENT.addConfigGroup( this.effectsGroup );
	}

	/** Checks whether enchantment books will have new texture with Wonderful Enchantments. */
	public boolean isEnchantedBookTextureReplacementEnabled() {
		return this.enchantmentBookReplacement.isEnabled();
	}
}
