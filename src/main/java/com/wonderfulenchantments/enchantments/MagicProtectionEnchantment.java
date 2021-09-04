package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

/** Enchantment that reduces damage from magic sources. (like Evoker fangs, Elder Guardian laser beam etc.) */
public class MagicProtectionEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig protectionBonus;

	public MagicProtectionEnchantment() {
		super( "magic_protection", Rarity.UNCOMMON, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "MagicProtection" );

		String comment = "Damage reduction bonus per enchantment level.";
		this.protectionBonus = new DoubleConfig( "armor_bonus", comment, false, 2.0, 1.0, 10.0 );

		this.enchantmentGroup.addConfig( this.protectionBonus );

		setMaximumEnchantmentLevel( 4 );
		setDifferenceBetweenMinimumAndMaximum( 11 );
		setMinimumEnchantabilityCalculator( level->( 1 + ( level - 1 ) * 11 ) );
	}

	@Override
	public int getDamageProtection( int level, DamageSource source ) {
		if( source.isCreativePlayer() )
			return 0;
		else if( source.isMagic() )
			return ( int )( level * this.protectionBonus.get() );

		return 0;
	}

	@Override
	protected boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof ProtectionEnchantment ) && super.checkCompatibility( enchantment );
	}
}
