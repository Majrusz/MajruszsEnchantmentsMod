package com.wonderfulenchantments.gamemodifiers;

import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EnchantmentConfig;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.WonderfulEnchantments;

public class EnchantmentModifier< EnchantmentType extends CustomEnchantment > extends GameModifier {
	public static final String ENCHANTMENT = WonderfulEnchantments.MOD_ID + "Enchantment";
	public static final String CURSE = WonderfulEnchantments.MOD_ID + "Curse";
	protected final EnchantmentType enchantment;
	final EnchantmentConfig enchantmentConfig;

	public EnchantmentModifier( EnchantmentType enchantment, String configName, String configComment ) {
		super( enchantment.getParams().isCurse() ? CURSE : ENCHANTMENT, configName, configComment );
		this.enchantment = enchantment;
		this.enchantmentConfig = new EnchantmentConfig( "" );

		this.addConfig( this.enchantmentConfig );
		this.enchantment.setEnabledSupplier( this.enchantmentConfig::isEnabled );

		Registries.GAME_MODIFIERS.add( this );
	}
}
