package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.loot_modifiers.LootHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Functionality of Smelter enchantment. */
public class SmeltingItems extends LootModifier {
	public SmeltingItems( LootItemCondition[] conditionsIn ) {
		super( conditionsIn );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = LootHelper.getParameter( context, LootContextParams.TOOL );
		Entity entity = LootHelper.getParameter( context, LootContextParams.THIS_ENTITY );
		if( tool == null || !( entity instanceof Player ) )
			return generatedLoot;

		Player player = ( Player )entity;
		if( player.isCrouching() )
			return generatedLoot;

		int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel( Enchantments.BLOCK_FORTUNE, tool );
		return getSmeltedLoot( generatedLoot, context.getLevel(), LootHelper.getParameter( context, LootContextParams.ORIGIN ), fortuneLevel );
	}

	/** Smelts given item stack if possible. */
	protected static ItemStack smeltIfPossible( ItemStack itemStack, ServerLevel world ) {
		return world.getRecipeManager()
			.getRecipeFor( RecipeType.SMELTING, new SimpleContainer( itemStack ), world )
			.map( SmeltingRecipe::getResultItem )
			.filter( i->!i.isEmpty() )
			.map( i->ItemHandlerHelper.copyStackWithSize( i, itemStack.getCount() * i.getCount() ) )
			.orElse( itemStack );
	}

	/**
	 Calculates random experience for smelted items.
	 For example if smelting recipe gives 0.4 XP and it has smelted 6 items.
	 0.4 XP * 6 = 2.4 XP
	 This will give player 2 XP point and has 40% (0.4) chance for another 1 XP point.
	 */
	protected static int calculateRandomExperienceForRecipe( SmeltingRecipe recipe, int smeltedItems ) {
		return Random.randomizeExperience( recipe.getExperience() * smeltedItems );
	}

	/** Returns smelted item stack. */
	protected static ItemStack getSmeltedItemStack( ItemStack itemStackToSmelt, ServerLevel world ) {
		ItemStack smeltedItemStack = smeltIfPossible( itemStackToSmelt, world );
		if( smeltedItemStack.getCount() != itemStackToSmelt.getCount() )
			smeltedItemStack.setCount( itemStackToSmelt.getCount() );

		return smeltedItemStack;
	}

	/** Gives a chance to increase item stack count if fortune level is high. */
	protected static void affectByFortune( int fortuneLevel, ItemStack itemStack ) {
		itemStack.setCount( itemStack.getCount() * ( 1 + MajruszLibrary.RANDOM.nextInt( fortuneLevel + 1 ) ) );
	}

	protected List< ItemStack > getSmeltedLoot( List< ItemStack > generatedLoot, ServerLevel world, Vec3 position, int fortuneLevel ) {
		RecipeManager recipeManager = world.getRecipeManager();

		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			ItemStack smeltedItemStack = getSmeltedItemStack( itemStack, world );
			if( isItemAffectedByFortune( itemStack.getItem() ) && fortuneLevel > 0 )
				affectByFortune( fortuneLevel, smeltedItemStack );

			output.add( smeltedItemStack );
			Optional< SmeltingRecipe > recipe = recipeManager.getRecipeFor( RecipeType.SMELTING, new SimpleContainer( itemStack ), world );

			if( !recipe.isPresent() )
				continue;

			int experience = calculateRandomExperienceForRecipe( recipe.get(), itemStack.getCount() );
			if( experience > 0 )
				world.addFreshEntity( new ExperienceOrb( world, position.x, position.y, position.z, experience ) );

			world.sendParticles( ParticleTypes.FLAME, position.x, position.y, position.z, 2 + MajruszLibrary.RANDOM.nextInt( 4 ), 0.125, 0.125, 0.125,
				0.03125
			);
		}

		return output;
	}

	/** Checks whether item should be affected by Fortune enchantment. */
	protected boolean isItemAffectedByFortune( Item item ) {
		if( Instances.SMELTER.isExtraLootDisabled() )
			return false;

		return Instances.SMELTER.shouldIncreaseLoot( item.getRegistryName() );
	}

	public static class Serializer extends GlobalLootModifierSerializer< SmeltingItems > {
		@Override
		public SmeltingItems read( ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn ) {
			return new SmeltingItems( conditionsIn );
		}

		@Override
		public JsonObject write( SmeltingItems instance ) {
			return null;
		}
	}
}
