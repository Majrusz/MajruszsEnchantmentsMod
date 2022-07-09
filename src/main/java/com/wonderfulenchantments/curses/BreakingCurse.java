package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnItemHurtContext;
import com.mlib.gamemodifiers.data.OnItemHurtData;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
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
		public Modifier( BreakingCurse enchantment ) {
			super( enchantment, "Breaking", "Makes all items break faster." );

			OnItemHurtContext onItemHurt = new OnItemHurtContext( this::dealExtraDamage );
			onItemHurt.addCondition( data->data.player != null )
				.addCondition( data->enchantment.hasEnchantment( data.player ) );

			this.addContext( onItemHurt );
		}

		private void dealExtraDamage( OnItemHurtData data ) {
			assert data.player != null;
			double damageMultiplier = this.enchantment.getEnchantmentLevel( data.itemStack ) * 6;
			data.event.extraDamage += Random.roundRandomly( data.event.damage * damageMultiplier );
		}
	}
}
