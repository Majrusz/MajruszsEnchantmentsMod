package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.wonderfulenchantments.ConfigHandlerOld.Config;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.util.DamageSource;

/** Enchantment that reduces damage from magic sources. (like Evoker fangs, Elder Guardian laser beam etc.) */
public class MagicProtectionEnchantment extends ProtectionEnchantment {
	public MagicProtectionEnchantment() {
		super( Rarity.UNCOMMON, Type.ALL, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int calcModifierDamage( int level, DamageSource source ) {
		if( source.canHarmInCreative() )
			return 0;
		else if( source.isMagicDamage() )
			return level * Config.MAGIC_PROTECTION_BONUS.get();

		return 0;
	}
}
