package com.majruszsenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnItemHurtContext;
import com.mlib.gamemodifiers.data.OnItemHurtData;
import com.mlib.gamemodifiers.parameters.ContextParameters;
import com.mlib.gamemodifiers.parameters.Priority;
import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class GoldFuelledEnchantment extends CustomEnchantment {
	public static Supplier< GoldFuelledEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.GOLDEN, EquipmentSlots.ARMOR_AND_HANDS, false, 1, level->15, level->45 );
		GoldFuelledEnchantment enchantment = new GoldFuelledEnchantment( params );
		Modifier modifier = new GoldFuelledEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public GoldFuelledEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< GoldFuelledEnchantment > {
		public Modifier( GoldFuelledEnchantment enchantment ) {
			super( enchantment, "GoldFuelled", "Completely repairs gold tools and armour for one gold ingot when the item is about to be destroyed." );

			OnItemHurtContext onItemHurt = new OnItemHurtContext( this::restoreItem, new ContextParameters( Priority.LOWEST, null, null ) );
			onItemHurt.addCondition( data->data.player != null )
				.addCondition( data->enchantment.hasEnchantment( data.itemStack ) )
				.addCondition( data->data.event.isAboutToBroke() );

			this.addContext( onItemHurt );
		}

		private void restoreItem( OnItemHurtData data ) {
			assert data.player != null;
			if( consumeGoldIngot( data.player ) ) {
				Vec3 position = data.player.position();
				data.player.level.playSound( null, position.x, position.y, position.z, SoundEvents.ITEM_BREAK, SoundSource.AMBIENT, 0.7f, 1.0f );
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
