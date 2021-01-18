package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Functionality of Telekinesis enchantment. */
public class AddItemsDirectlyToInventory extends LootModifier {
	public AddItemsDirectlyToInventory( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = context.get( LootParameters.TOOL );
		if( tool == null )
			return generatedLoot;

		Entity entity = context.get( LootParameters.THIS_ENTITY );
		if( !( entity instanceof PlayerEntity ) )
			return generatedLoot;

		PlayerEntity player = ( PlayerEntity )entity;
		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			if( !player.canPickUpItem( itemStack ) )
				output.add( itemStack );

			player.inventory.addItemStackToInventory( itemStack );
		}

		return output;
	}

	public static class Serializer extends GlobalLootModifierSerializer< AddItemsDirectlyToInventory > {
		@Override
		public AddItemsDirectlyToInventory read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			return new AddItemsDirectlyToInventory( conditions );
		}

		@Override
		public JsonObject write( AddItemsDirectlyToInventory instance ) {
			return null;
		}
	}
}
