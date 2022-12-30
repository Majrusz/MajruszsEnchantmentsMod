package com.majruszsenchantments.gamemodifiers;

import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.ContextData;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EnchantmentConfig;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EnchantmentModifier< EnchantmentType extends CustomEnchantment > extends GameModifier {
	protected final RegistryObject< EnchantmentType > enchantment;
	protected final EnchantmentConfig enchantmentConfig = new EnchantmentConfig();

	public EnchantmentModifier( RegistryObject< EnchantmentType > enchantment, String key ) {
		super( key );

		this.enchantment = enchantment;

		this.addConfig( this.enchantmentConfig );
	}

	public Supplier< Boolean > getEnabledSupplier() {
		return this.enchantmentConfig::isEnabled;
	}

	protected < Type extends ContextData > boolean isEnchantmentEnabled( Type data ) {
		return this.enchantmentConfig.isEnabled();
	}
}
