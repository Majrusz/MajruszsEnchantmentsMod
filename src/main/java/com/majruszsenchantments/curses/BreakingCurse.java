package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnItemHurt;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BreakingCurse extends CustomEnchantment {
	public BreakingCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DigDurabilityEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< BreakingCurse > {
		final DoubleConfig damageMultiplier = new DoubleConfig( 1.0, new Range<>( 0.0, 10.0 ) );

		public Modifier() {
			super( Registries.BREAKING, Registries.Modifiers.CURSE );

			new OnItemHurt.Context( this::dealExtraDamage )
				.addCondition( data->data.player != null )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addConfig( this.damageMultiplier.name( "damage_multiplier" ).comment( "Extra damage multiplier per enchantment level." ) )
				.insertTo( this );

			this.name( "Breaking" ).comment( "Makes all items break faster." );
		}

		private void dealExtraDamage( OnItemHurt.Data data ) {
			assert data.player != null;
			double damageMultiplier = this.enchantment.get().getEnchantmentLevel( data.itemStack ) * this.damageMultiplier.get();
			data.event.extraDamage += Random.roundRandomly( data.event.damage * damageMultiplier );
		}
	}
}
