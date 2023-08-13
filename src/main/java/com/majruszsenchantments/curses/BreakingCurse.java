package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.contexts.OnItemHurt;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class BreakingCurse extends CustomEnchantment {
	public BreakingCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DigDurabilityEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig damageMultiplier = new DoubleConfig( 1.0, new Range<>( 0.0, 10.0 ) );
		final Supplier< BreakingCurse > enchantment = Registries.BREAKING;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Breaking" )
				.comment( "Makes all items break faster." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnItemHurt.listen( this::dealExtraDamage )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addConfig( this.damageMultiplier.name( "damage_multiplier" ).comment( "Extra damage multiplier per enchantment level." ) )
				.insertTo( group );
		}

		private void dealExtraDamage( OnItemHurt.Data data ) {
			assert data.player != null;
			double damageMultiplier = this.enchantment.get().getEnchantmentLevel( data.itemStack ) * this.damageMultiplier.get();
			data.extraDamage += Random.round( data.damage * damageMultiplier );
		}
	}
}
