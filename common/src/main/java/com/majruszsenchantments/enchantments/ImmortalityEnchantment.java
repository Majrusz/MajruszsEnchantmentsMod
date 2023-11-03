package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityDied;
import com.mlib.contexts.base.Condition;
import com.mlib.entity.EntityHelper;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.ItemHelper;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class ImmortalityEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_SHIELD )
			.slots( EquipmentSlots.HANDS )
			.minLevelCost( level->20 )
			.maxLevelCost( level->50 );
	}

	public ImmortalityEnchantment() {
		super( MajruszsEnchantments.IMMORTALITY, false );

		OnEntityDied.listen( this::cheatDeath )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.target ) );
	}

	private void cheatDeath( OnEntityDied data ) {
		EnchantmentHelper.remove( this.enchantment, ItemHelper.getHandItem( data.target, itemStack->EnchantmentHelper.has( this.enchantment, itemStack ) ) );
		EntityHelper.cheatDeath( data.target );

		data.cancelDeath();
	}
}
