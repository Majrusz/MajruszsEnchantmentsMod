package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.util.DamageSource;

public class MagicProtectionEnchantment extends ProtectionEnchantment {
	public MagicProtectionEnchantment() {
		super( Rarity.UNCOMMON, Type.ALL, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int calcModifierDamage( int level, DamageSource source ) {
		if( source.canHarmInCreative() )
			return 0;
		else if( source.isMagicDamage() )
			return level * 2;

		return 0;
	}
}
