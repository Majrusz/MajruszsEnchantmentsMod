package com.majruszsenchantments.gamemodifiers;

import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.configs.EnchantmentConfig;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EnchantmentModifier< EnchantmentType extends CustomEnchantment > extends GameModifier {
	protected final RegistryObject< EnchantmentType > enchantment;
	protected final EnchantmentConfig enchantmentConfig = new EnchantmentConfig( "" );

	public EnchantmentModifier( RegistryObject< EnchantmentType > enchantment, String key, String name, String comment ) {
		super( key, name, comment );

		this.enchantment = enchantment;

		this.addConfig( this.enchantmentConfig );
	}

	public Supplier< Boolean > getEnabledSupplier() {
		return this.enchantmentConfig::isEnabled;
	}
}
