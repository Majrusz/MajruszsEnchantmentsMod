package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SmeltingItems extends LootModifier {
	public SmeltingItems( ILootCondition[] conditionsIn ) {
		super( conditionsIn );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = context.get( LootParameters.TOOL );
		if( tool == null )
			return generatedLoot;

		ServerWorld world = context.getWorld();

		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			output.add( smelt( itemStack, context ) );

			Optional< FurnaceRecipe > recipe = world.getRecipeManager()
				.getRecipe( IRecipeType.SMELTING, new Inventory( itemStack ), world );

			if( recipe.isPresent() ) {
				BlockPos position = new BlockPos( context.get( LootParameters.field_237457_g_ ) );
				int experience = ( recipe.get()
					.getExperience() > WonderfulEnchantments.RANDOM.nextFloat() ? 1 : 0
				);

				if( experience > 0 )
					world.addEntity(
						new ExperienceOrbEntity( world, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, experience ) );
				world.spawnParticle( ParticleTypes.FLAME, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D,
					2 + WonderfulEnchantments.RANDOM.nextInt( 4 ), 0.125D, 0.125D, 0.125D, 0.03125D
				);
			}
		}

		return output;
	}

	protected static ItemStack smelt( ItemStack itemStack, LootContext lootContext ) {
		return lootContext.getWorld()
			.getRecipeManager()
			.getRecipe( IRecipeType.SMELTING, new Inventory( itemStack ), lootContext.getWorld() )
			.map( FurnaceRecipe::getRecipeOutput )
			.filter( i->!i.isEmpty() )
			.map( i->ItemHandlerHelper.copyStackWithSize( i, i.getCount() * i.getCount() ) )
			.orElse( itemStack );
	}

	public static class Serializer extends GlobalLootModifierSerializer< SmeltingItems > {
		@Override
		public SmeltingItems read( ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn ) {
			return new SmeltingItems( conditionsIn );
		}

		@Override
		public JsonObject write( SmeltingItems instance ) {
			return null;
		}
	}
}
