package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

import java.util.function.Supplier;

public class MagicProtectionEnchantment extends CustomEnchantment {
	public MagicProtectionEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 11 - 10 )
			.maxLevelCost( level->level * 11 + 1 );
	}

	@Override
	public int getDamageProtection( int level, DamageSource source ) {
		return !source.isCreativePlayer() && source.is( DamageTypes.MAGIC ) ? level * 2 : 0;
	}

	@Override
	protected boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof ProtectionEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Handler {
		final Supplier< MagicProtectionEnchantment > enchantment = Registries.MAGIC_PROTECTION;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "MagicProtection" )
				.comment( "Protects against magical damage like Evoker Fangs, Guardians and Instant Damage potions." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );
		}
	}
}
