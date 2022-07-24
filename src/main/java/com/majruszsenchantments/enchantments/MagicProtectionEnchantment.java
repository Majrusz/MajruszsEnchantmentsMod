package com.majruszsenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

import java.util.function.Supplier;

public class MagicProtectionEnchantment extends CustomEnchantment {
	public static Supplier< MagicProtectionEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, false, 4, level->-10 + level * 11, level->1 + level * 11 );
		MagicProtectionEnchantment enchantment = new MagicProtectionEnchantment( params );
		Modifier modifier = new MagicProtectionEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public MagicProtectionEnchantment( Parameters params ) {
		super( params );
	}

	@Override
	public int getDamageProtection( int level, DamageSource source ) {
		return !source.isCreativePlayer() && source.isMagic() ? level * 2 : 0;
	}

	@Override
	protected boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof ProtectionEnchantment ) && super.checkCompatibility( enchantment );
	}

	private static class Modifier extends EnchantmentModifier< MagicProtectionEnchantment > {
		public Modifier( MagicProtectionEnchantment enchantment ) {
			super( enchantment, "MagicProtection", "Protects against magical damage like Evoker Fangs, Guardians and Instant Damage potions." );
		}
	}
}
