package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Functionality of Smelter enchantment. */
public class SmeltingItems extends LootModifier {
	private final List< String > extraItemsToWorkWithFortune;

	public SmeltingItems( ILootCondition[] conditionsIn, List< String > extraItemsToWorkWithFortune ) {
		super( conditionsIn );

		this.extraItemsToWorkWithFortune = extraItemsToWorkWithFortune;
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = context.get( LootParameters.TOOL );
		if( tool == null )
			return generatedLoot;

		int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE, tool );
		return getSmeltedLoot( generatedLoot, context.getWorld(), context.get( LootParameters.field_237457_g_ ), fortuneLevel );
	}

	/** Smelts given item stack if possible. */
	protected static ItemStack smeltIfPossible( ItemStack itemStack, ServerWorld world ) {
		return world.getRecipeManager()
			.getRecipe( IRecipeType.SMELTING, new Inventory( itemStack ), world )
			.map( FurnaceRecipe::getRecipeOutput )
			.filter( i->!i.isEmpty() )
			.map( i->ItemHandlerHelper.copyStackWithSize( i, i.getCount() * i.getCount() ) )
			.orElse( itemStack );
	}

	/**
	 Calculates random experience for smelted items.
	 For example if smelting recipe gives 0.4 XP and it has smelted 6 items.
	 0.4 XP * 6 = 2.4 XP
	 This will give player 2 XP point and has 40% (0.4) chance for another 1 XP point.
	 */
	protected static int calculateRandomExperienceForRecipe( FurnaceRecipe recipe, int smeltedItems ) {
		return Random.randomizeExperience( recipe.getExperience() * smeltedItems );
	}

	/** Returns smelted item stack. */
	protected static ItemStack getSmeltedItemStack( ItemStack itemStackToSmelt, ServerWorld world ) {
		ItemStack smeltedItemStack = smeltIfPossible( itemStackToSmelt, world );
		if( smeltedItemStack.getCount() != itemStackToSmelt.getCount() )
			smeltedItemStack.setCount( itemStackToSmelt.getCount() );

		return smeltedItemStack;
	}

	/** Gives a chance to increase item stack count if fortune level is high. */
	protected static void affectByFortune( int fortuneLevel, ItemStack itemStack ) {
		itemStack.setCount( itemStack.getCount() * ( 1 + MajruszLibrary.RANDOM.nextInt( fortuneLevel + 1 ) ) );
	}

	protected List< ItemStack > getSmeltedLoot( List< ItemStack > generatedLoot, ServerWorld world, Vector3d position, int fortuneLevel ) {
		RecipeManager recipeManager = world.getRecipeManager();
		
		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			ItemStack smeltedItemStack = getSmeltedItemStack( itemStack, world );
			if( isItemAffectedByFortune( itemStack.getItem() ) && fortuneLevel > 0 )
				affectByFortune( fortuneLevel, smeltedItemStack );

			output.add( smeltedItemStack );
			Optional< FurnaceRecipe > recipe = recipeManager.getRecipe( IRecipeType.SMELTING, new Inventory( itemStack ), world );

			if( !recipe.isPresent() )
				continue;

			int experience = calculateRandomExperienceForRecipe( recipe.get(), itemStack.getCount() );
			if( experience > 0 )
				world.addEntity( new ExperienceOrbEntity( world, position.x, position.y, position.z, experience ) );

			world.spawnParticle( ParticleTypes.FLAME, position.x, position.y, position.z,
				2 + MajruszLibrary.RANDOM.nextInt( 4 ), 0.125, 0.125, 0.125, 0.03125
			);
		}

		return output;
	}

	/** Checks whether item should be affected by Fortune enchantment. */
	protected boolean isItemAffectedByFortune( Item item ) {
		if( Instances.SMELTER.isExtraLootDisabled() )
			return false;

		for( String registerName : this.extraItemsToWorkWithFortune ) {
			ResourceLocation itemResourceLocation = item.getRegistryName();
			if( itemResourceLocation != null && itemResourceLocation.toString()
				.equals( registerName.substring( 1, registerName.length() - 1 ) ) )
				return true;
		}

		return false;
	}

	public static class Serializer extends GlobalLootModifierSerializer< SmeltingItems > {
		@Override
		public SmeltingItems read( ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn ) {
			List< String > extraItemsToWorkWithFortune = new ArrayList<>();
			JsonArray jsonArray = JSONUtils.getJsonArray( object, "fortuneBonus" );
			for( JsonElement element : jsonArray )
				extraItemsToWorkWithFortune.add( element.toString() );

			return new SmeltingItems( conditionsIn, extraItemsToWorkWithFortune );
		}

		@Override
		public JsonObject write( SmeltingItems instance ) {
			return null;
		}
	}
}
