package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnItemHurt;
import com.mlib.gamemodifiers.parameters.Priority;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class GoldFuelledEnchantment extends CustomEnchantment {
	public GoldFuelledEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.GOLDEN )
			.slots( EquipmentSlots.ALL )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< GoldFuelledEnchantment > {
		public Modifier() {
			super( Registries.GOLD_FUELLED, Registries.Modifiers.ENCHANTMENT );

			new OnItemHurt.Context( this::restoreItem )
				.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( data->data.player != null )
				.addCondition( data->this.enchantment.get().hasEnchantment( data.itemStack ) )
				.addCondition( data->data.event.isAboutToBroke() )
				.insertTo( this );

			this.name( "GoldFuelled" ).comment( "Completely repairs gold tools and armour for one gold ingot when the item is about to be destroyed." );
		}

		private void restoreItem( OnItemHurt.Data data ) {
			assert data.player != null;
			if( this.consumeGoldIngot( data.player ) ) {
				Vec3 position = data.player.position();
				SoundHandler.ITEM_BREAK.play( data.level, position );
				data.event.extraDamage = -data.itemStack.getMaxDamage(); // restores initial durability
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
