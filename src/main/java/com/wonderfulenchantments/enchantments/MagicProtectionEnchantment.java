package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.util.DamageSource;

/** Enchantment that reduces damage from magic sources. (like Evoker fangs, Elder Guardian laser beam etc.) */
public class MagicProtectionEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig protectionBonus;

	public MagicProtectionEnchantment() {
		super( "magic_protection", Rarity.UNCOMMON, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "MagicProtection" );
		String comment = "Damage reduction bonus per enchantment level.";
		this.protectionBonus = new DoubleConfig( "armor_bonus", comment, false, 2.0, 1.0, 10.0 );
		this.enchantmentGroup.addConfig( this.protectionBonus );

		setMaximumEnchantmentLevel( 4 );
		setDifferenceBetweenMinimumAndMaximum( 11 );
		setMinimumEnchantabilityCalculator( level->( 1 + ( level - 1 ) * 11 ) );
	}

	@Override
	public int calcModifierDamage( int level, DamageSource source ) {
		if( source.canHarmInCreative() )
			return 0;
		else if( source.isMagicDamage() )
			return ( int )( level * Instances.MAGIC_PROTECTION.protectionBonus.get() );

		return 0;
	}

	@Override
	protected boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof ProtectionEnchantment ) && super.canApplyTogether( enchantment );
	}
}
