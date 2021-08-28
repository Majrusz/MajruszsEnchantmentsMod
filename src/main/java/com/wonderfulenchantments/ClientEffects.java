package com.wonderfulenchantments;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;

/** All client-side effects manager. */
public class ClientEffects {
	protected final ConfigGroup effectsGroup;
	protected final AvailabilityConfig enchantmentBookReplacement;
	protected final AvailabilityConfig combinedBookReplacement;

	public ClientEffects() {
		String bookComment = "Should the Enchanted Book has a different texture when it has any of the Wonderful Enchantments on it? (disabling it may fix some bugs with other mods) (requires world/game restart)";
		this.enchantmentBookReplacement = new AvailabilityConfig( "enchantment_book_replacement", bookComment, true, true );

		String combinedComment = "Should the Enchanted Book has a different texture when it has any of the Wonderful Enchantments and other enchantments on it? (disabling it may fix some bugs with other mods) (requires world/game restart)";
		this.combinedBookReplacement = new AvailabilityConfig( "combined_book_replacement", combinedComment, true, true );

		this.effectsGroup = new ConfigGroup( "VisualEffects", "" );
		this.effectsGroup.addConfigs( this.enchantmentBookReplacement, this.combinedBookReplacement );

		WonderfulEnchantments.CONFIG_HANDLER_CLIENT.addConfigGroup( this.effectsGroup );
	}

	/** Checks whether enchantment book should has a new texture when it has any of the Wonderful Enchantments on it. */
	public boolean isEnchantedBookTextureReplacementEnabled() {
		return this.enchantmentBookReplacement.isEnabled();
	}

	/** Checks whether enchantment book should has a new texture when it has any of the Wonderful Enchantments and other enchantments on it. */
	public boolean isCombinedBookTextureReplacementEnabled() {
		return this.combinedBookReplacement.isEnabled();
	}
}
