package com.majruszsenchantments.curses;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Range;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class SlipperyCurse extends Handler {
	float chance = 0.03f;
	float cooldown = 1.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_TOOL )
			.slots( EquipmentSlots.HANDS )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	public SlipperyCurse() {
		super( MajruszsEnchantments.SLIPPERY, true );

		OnEntityTicked.listen( this::dropItem )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( ()->this.cooldown ) )
			.addCondition( Condition.chance( ()->this.chance ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) );

		this.config.defineFloat( "drop_chance", ()->this.chance, x->this.chance = Range.CHANCE.clamp( x ) );
		this.config.defineFloat( "drop_cooldown", ()->this.cooldown, x->this.cooldown = Range.of( 0.05f, 60.0f ).clamp( x ) );
	}

	private void dropItem( OnEntityTicked data ) {
		EquipmentSlot slot = EnchantmentHelper.has( this.enchantment, data.entity.getMainHandItem() ) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		ItemStack itemStack = data.entity.getItemBySlot( slot );
		if( data.entity instanceof Player player ) {
			player.drop( itemStack, false );
		} else {
			ItemEntity entity = new ItemEntity( data.getLevel(), data.entity.getX(), data.entity.getX(), data.entity.getX(), itemStack );
			entity.setPickUpDelay( 40 );
		}
		data.entity.setItemSlot( slot, ItemStack.EMPTY );
	}
}
