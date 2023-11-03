package com.majruszsenchantments.curses;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnItemDamaged;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Priority;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class BreakingCurse extends Handler {
	float damageMultiplier = 1.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.compatibility( enchantment->!( enchantment instanceof DigDurabilityEnchantment ) );
	}

	public BreakingCurse() {
		super( MajruszsEnchantments.BREAKING, true );

		OnItemDamaged.listen( this::dealExtraDamage )
			.priority( Priority.HIGH )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) );

		this.config.defineFloat( "damage_multiplier_per_level", ()->this.damageMultiplier, x->this.damageMultiplier = Range.of( 0.0f, 10.0f ).clamp( x ) );
	}

	private void dealExtraDamage( OnItemDamaged data ) {
		data.damage += Random.round( data.damage * EnchantmentHelper.getLevel( this.enchantment, data.itemStack ) * this.damageMultiplier );
	}
}
