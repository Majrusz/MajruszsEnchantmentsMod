package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.Priority;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnItemHurt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class GoldFuelledEnchantment extends CustomEnchantment {
	public GoldFuelledEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.GOLDEN )
			.slots( EquipmentSlots.ALL )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	@AutoInstance
	public static class Handler {
		final Supplier< GoldFuelledEnchantment > enchantment = Registries.GOLD_FUELLED;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "GoldFuelled" )
				.comment( "Completely repairs gold tools and armour for one gold ingot when the item is about to be destroyed." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnItemHurt.listen( this::restoreItem )
				.priority( Priority.LOWEST )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.player != null ) )
				.addCondition( Condition.predicate( data->this.enchantment.get().hasEnchantment( data.itemStack ) ) )
				.addCondition( Condition.predicate( OnItemHurt.Data::isAboutToBroke ) )
				.insertTo( group );
		}

		private void restoreItem( OnItemHurt.Data data ) {
			assert data.player != null;
			if( this.consumeGoldIngot( data.player ) ) {
				Vec3 position = data.player.position();
				SoundHandler.ITEM_BREAK.play( data.getLevel(), position );
				data.extraDamage = -data.itemStack.getMaxDamage(); // restores initial durability
			}
		}

		private boolean consumeGoldIngot( ServerPlayer player ) {
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
}
