package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnItemHurtContext;
import com.mlib.gamemodifiers.data.OnItemHurtData;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class BreakingCurse extends CustomEnchantment {
	public static Supplier< BreakingCurse > create() {
		Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlots.ARMOR_AND_HANDS, true, 3, level->10, level->50 );
		BreakingCurse enchantment = new BreakingCurse( params );
		Modifier modifier = new BreakingCurse.Modifier( enchantment );

		return ()->enchantment;
	}

	public BreakingCurse( Parameters params ) {
		super( params );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DigDurabilityEnchantment ) && super.checkCompatibility( enchantment );
	}

	private static class Modifier extends EnchantmentModifier< BreakingCurse > {
		final DoubleConfig damageMultiplier = new DoubleConfig( "damage_multiplier", "Damage multiplier per enchantment level.", false, 1.0, 0.0, 10.0 );

		public Modifier( BreakingCurse enchantment ) {
			super( enchantment, "Breaking", "Makes all items break faster." );

			OnItemHurtContext onItemHurt = new OnItemHurtContext( this::dealExtraDamage );
			onItemHurt.addCondition( data->data.player != null ).addCondition( data->enchantment.hasEnchantment( data.player ) );

			this.addConfig( this.damageMultiplier );
			this.addContext( onItemHurt );
		}

		private void dealExtraDamage( OnItemHurtData data ) {
			assert data.player != null;
			double damageMultiplier = this.enchantment.getEnchantmentLevel( data.itemStack ) * this.damageMultiplier.get();
			data.event.extraDamage += Random.roundRandomly( data.event.damage * damageMultiplier );
		}
	}
}
