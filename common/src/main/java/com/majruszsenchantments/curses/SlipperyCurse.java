package com.majruszsenchantments.curses;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
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
		super( MajruszsEnchantments.SLIPPERY, SlipperyCurse.class, true );

		OnEntityTicked.listen( this::dropItem )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( ()->this.cooldown ) )
			.addCondition( Condition.chance( ()->this.chance ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) );

		this.config.define( "drop_chance", Reader.number(), s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) )
			.define( "drop_cooldown", Reader.number(), s->this.cooldown, ( s, v )->this.cooldown = Range.of( 0.05f, 60.0f ).clamp( v ) );
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
