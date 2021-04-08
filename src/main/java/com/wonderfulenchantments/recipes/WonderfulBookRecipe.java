package com.wonderfulenchantments.recipes;

import com.google.common.collect.Lists;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.items.WonderfulBookItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

/** Recipe for increasing the energy of the book. */
public class WonderfulBookRecipe extends SpecialRecipe {
	public WonderfulBookRecipe( ResourceLocation id ) {
		super( id );
	}

	/** Used to check if a recipe matches current crafting inventory. */
	public boolean matches( CraftingInventory inventory, World world ) {
		ItemStack wonderfulBook = ItemStack.EMPTY;
		List< ItemStack > list = Lists.newArrayList();

		for( int i = 0; i < inventory.getSizeInventory(); ++i ) {
			ItemStack itemStack = inventory.getStackInSlot( i );
			if( itemStack.isEmpty() )
				continue;

			if( itemStack.getItem() instanceof WonderfulBookItem ) {
				if( !wonderfulBook.isEmpty() )
					return false;

				wonderfulBook = itemStack;
			} else {
				if( !canEnergize( itemStack ) )
					return false;

				list.add( itemStack );
			}
		}

		return !wonderfulBook.isEmpty() && !list.isEmpty();
	}

	/** Returns an Item that is the result of this recipe. */
	public ItemStack getCraftingResult( CraftingInventory inventory ) {
		ItemStack wonderfulBook = ItemStack.EMPTY;
		List< ItemStack > list = Lists.newArrayList();

		for( int i = 0; i < inventory.getSizeInventory(); ++i ) {
			ItemStack itemStack = inventory.getStackInSlot( i );
			if( itemStack.isEmpty() )
				continue;

			Item item = itemStack.getItem();
			if( item instanceof WonderfulBookItem ) {
				if( !wonderfulBook.isEmpty() )
					return ItemStack.EMPTY;

				wonderfulBook = itemStack.copy();
			} else {
				if( !canEnergize( itemStack ) )
					return ItemStack.EMPTY;

				list.add( itemStack );
			}
		}

		return !wonderfulBook.isEmpty() && !list.isEmpty() ? wonderfulBook : ItemStack.EMPTY;
	}

	/** Used to determine if this recipe can fit in a grid of the given width/height. */
	public boolean canFit( int width, int height ) {
		return width * height >= 2;
	}

	/** Returns instance of recipe serializer. */
	public IRecipeSerializer< ? > getSerializer() {
		return Instances.WONDERFUL_BOOK_RECIPE;
	}

	/** Checks whether item is used for energizing Ultimate Book. */
	private boolean canEnergize( ItemStack itemStack ) {
		return itemStack.getItem() == Items.LAPIS_LAZULI;
	}
}
