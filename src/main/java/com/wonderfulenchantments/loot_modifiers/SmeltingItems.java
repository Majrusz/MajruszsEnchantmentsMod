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
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.JSONUtils;
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

		ServerWorld world = context.getWorld();
		int fortuneLevel = EnchantmentHelper.getEnchantmentLevel( Enchantments.FORTUNE, tool );

		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			ItemStack smeltedItemStack = getSmeltedItemStack( itemStack, context );
			if( isItemAffectedByFortune( itemStack.getItem() ) )
				affectByFortuneIfPossible( fortuneLevel, smeltedItemStack );
			output.add( smeltedItemStack );

			Optional< FurnaceRecipe > recipe = world.getRecipeManager()
				.getRecipe( IRecipeType.SMELTING, new Inventory( itemStack ), world );

			if( recipe.isPresent() ) {
				BlockPos position = new BlockPos( context.get( LootParameters.field_237457_g_ ) );
				int experience = calculateRandomExperienceForRecipe( recipe.get(), itemStack.getCount() );

				if( experience > 0 )
					world.addEntity(
						new ExperienceOrbEntity( world, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, experience ) );
				world.spawnParticle( ParticleTypes.FLAME, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D,
					2 + MajruszLibrary.RANDOM.nextInt( 4 ), 0.125D, 0.125D, 0.125D, 0.03125D
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

	/**
	 Calculates random experience for smelted items.
	 For example if smelting recipe gives 0.4 XP and it has smelted 4 items.
	 0.4 XP * 4 = 1.6 XP
	 This will give player 1 XP point and has 60% (0.6) chance for another 1 XP point.
	 */
	protected int calculateRandomExperienceForRecipe( FurnaceRecipe recipe, int smeltedItems ) {
		double recipeExperience = recipe.getExperience() * smeltedItems;
		int experience = ( int )( recipeExperience );
		if( Random.tryChance( recipeExperience - experience ) )
			experience++;

		return experience;
	}

	/** Returns smelted item stack. */
	protected ItemStack getSmeltedItemStack( ItemStack itemStackToSmelt, LootContext context ) {
		ItemStack smeltedItemStack = smelt( itemStackToSmelt, context );
		if( smeltedItemStack.getCount() != itemStackToSmelt.getCount() )
			smeltedItemStack.setCount( itemStackToSmelt.getCount() );

		return smeltedItemStack;
	}

	/** Gives a chance to increase item stack count if fortune level is high. */
	protected void affectByFortuneIfPossible( int fortuneLevel, ItemStack itemStack ) {
		if( fortuneLevel <= 0 || Instances.SMELTER.isExtraLootDisabled() )
			return;

		itemStack.setCount( itemStack.getCount() * ( 1 + MajruszLibrary.RANDOM.nextInt( fortuneLevel + 1 ) ) );
	}

	/** Checks whether item should be affected by Fortune enchantment. */
	protected boolean isItemAffectedByFortune( Item item ) {
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
