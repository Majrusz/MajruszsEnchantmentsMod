package com.wonderfulenchantments.recipes;

import com.google.common.collect.Lists;
import com.wonderfulenchantments.Instances;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

/** Recipe for increasing the energy of the book. */
public class UltimateBookRecipe extends SpecialRecipe {
	public UltimateBookRecipe( ResourceLocation id ) {
		super( id );
	}

	/** Used to check if a recipe matches current crafting inventory. */
	public boolean matches( CraftingInventory inventory, World world ) {
		ItemStack itemstack = ItemStack.EMPTY;
		List< ItemStack > list = Lists.newArrayList();

		for( int i = 0; i < inventory.getSizeInventory(); ++i ) {
			ItemStack itemstack1 = inventory.getStackInSlot( i );
			if( !itemstack1.isEmpty() ) {
				if( itemstack1.getItem() instanceof IDyeableArmorItem ) {
					if( !itemstack.isEmpty() ) {
						return false;
					}

					itemstack = itemstack1;
				} else {
					if( !( itemstack1.getItem() instanceof DyeItem ) ) {
						return false;
					}

					list.add( itemstack1 );
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty();
	}

	/** Returns an Item that is the result of this recipe. */
	public ItemStack getCraftingResult( CraftingInventory inventory ) {
		List< DyeItem > list = Lists.newArrayList();
		ItemStack itemstack = ItemStack.EMPTY;

		for( int i = 0; i < inventory.getSizeInventory(); ++i ) {
			ItemStack itemstack1 = inventory.getStackInSlot( i );
			if( !itemstack1.isEmpty() ) {
				Item item = itemstack1.getItem();
				if( item instanceof IDyeableArmorItem ) {
					if( !itemstack.isEmpty() ) {
						return ItemStack.EMPTY;
					}

					itemstack = itemstack1.copy();
				} else {
					if( !( item instanceof DyeItem ) ) {
						return ItemStack.EMPTY;
					}

					list.add( ( DyeItem )item );
				}
			}
		}

		return !itemstack.isEmpty() && !list.isEmpty() ? IDyeableArmorItem.dyeItem( itemstack, list ) : ItemStack.EMPTY;
	}

	/** Used to determine if this recipe can fit in a grid of the given width/height. */
	public boolean canFit( int width, int height ) {
		return width * height >= 2;
	}

	/** Returns instance of recipe serializer. */
	public IRecipeSerializer< ? > getSerializer() {
		return Instances.ULTIMATE_BOOK_RECIPE;
	}
}
