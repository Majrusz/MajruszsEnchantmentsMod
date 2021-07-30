package com.wonderfulenchantments.recipes;

import com.google.common.collect.Lists;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.items.WonderfulBookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

/** Recipe for increasing the energy of the book. */
public class WonderfulBookRecipe extends CustomRecipe {
	public WonderfulBookRecipe( ResourceLocation id ) {
		super( id );
	}

	/** Used to check if a recipe matches current crafting inventory. */
	@Override
	public boolean matches( CraftingContainer inventory, Level world ) {
		ItemStack wonderfulBook = ItemStack.EMPTY;
		List< ItemStack > list = Lists.newArrayList();

		for( int i = 0; i < inventory.getContainerSize(); ++i ) {
			ItemStack itemStack = inventory.getItem( i );
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

		return isValid( wonderfulBook, list );
	}

	@Override
	public ItemStack assemble( CraftingContainer container ) {
		return null;
	}

	/** Returns an Item that is the result of this recipe. */
	public ItemStack getCraftingResult( CraftingContainer inventory ) {
		ItemStack wonderfulBook = ItemStack.EMPTY;
		List< ItemStack > list = Lists.newArrayList();

		for( int i = 0; i < inventory.getContainerSize(); ++i ) {
			ItemStack itemStack = inventory.getItem( i );
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

		return isValid( wonderfulBook, list ) ? Instances.WONDERFUL_BOOK_ITEM.energizeBook( wonderfulBook, list ) : ItemStack.EMPTY;
	}

	/** Used to determine if this recipe can fit in a grid of the given width/height. */
	@Override
	public boolean canCraftInDimensions( int width, int height ) {
		return width * height >= 2;
	}

	/** Returns instance of recipe serializer. */
	@Override
	public RecipeSerializer< ? > getSerializer() {
		return Instances.WONDERFUL_BOOK_RECIPE;
	}

	/** Checks whether item is used for energizing Ultimate Book. */
	private boolean canEnergize( ItemStack itemStack ) {
		return itemStack.getItem() == Items.LAPIS_LAZULI;
	}

	/** Checks whether recipe is valid. */
	private boolean isValid( ItemStack itemStack, List< ItemStack > itemList ) {
		return !Instances.WONDERFUL_BOOK_ITEM.hasMaximumEnergyLevel( itemStack ) && !itemStack.isEmpty() && !itemList.isEmpty();
	}
}
