package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class MagicProtectionEnchantment extends CustomEnchantment {
	public MagicProtectionEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 11 - 10 )
			.maxLevelCost( level->level * 11 + 1 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public int getDamageProtection( int level, DamageSource source ) {
		return !source.isCreativePlayer() && source.isMagic() ? level * 2 : 0;
	}

	@Override
	protected boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof ProtectionEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< MagicProtectionEnchantment > {
		public Modifier() {
			super( Registries.MAGIC_PROTECTION, Registries.Modifiers.ENCHANTMENT );

			this.name( "MagicProtection" ).comment( "Protects against magical damage like Evoker Fangs, Guardians and Instant Damage potions." );
		}
	}
}
