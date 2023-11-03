package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnItemDamaged;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.Priority;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class GoldFuelledEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_GOLDEN )
			.slots( EquipmentSlots.ALL )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	public GoldFuelledEnchantment() {
		super( MajruszsEnchantments.GOLD_FUELLED, false );

		OnItemDamaged.listen( this::restoreInitialDurability )
			.priority( Priority.LOW )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.player != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.itemStack ) )
			.addCondition( OnItemDamaged::isAboutToBroke );
	}

	private void restoreInitialDurability( OnItemDamaged data ) {
		if( this.consumeGoldIngot( data.player ) ) {
			data.damage -= data.itemStack.getMaxDamage();
		}
	}

	private boolean consumeGoldIngot( Player player ) {
		for( Slot slot : player.inventoryMenu.slots ) {
			ItemStack itemStack = slot.getItem();
			if( itemStack.getItem() == Items.GOLD_INGOT ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				return true;
			}
		}

		return false;
	}
}
