package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EquipmentSlots;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

@AutoInstance
public class MagicProtectionEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment() {
			@Override
			public int getDamageProtection( int level, DamageSource source ) {
				return !source.isCreativePlayer() && source.isMagic() ? level * 2 : 0;
			}
		}
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 11 - 10 )
			.maxLevelCost( level->level * 11 + 1 )
			.compatibility( enchantment->!( enchantment instanceof ProtectionEnchantment ) );
	}

	public MagicProtectionEnchantment() {
		super( MajruszsEnchantments.MAGIC_PROTECTION, MagicProtectionEnchantment.class, false );
	}
}
