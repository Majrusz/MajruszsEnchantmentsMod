package com.wonderfulenchantments;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		IForgeRegistry< GlobalLootModifierSerializer< ? > > registry = event.getRegistry();

		registry.register( new SmeltingItems.Serializer().setRegistryName( new ResourceLocation( WonderfulEnchantments.MOD_ID, "smelter_enchantment" ) ) );
		registry.register( new AddItemsDirectlyToInventory.Serializer().setRegistryName( new ResourceLocation( WonderfulEnchantments.MOD_ID, "telekinesis_enchantment" ) ) );
	}

	private static class SmeltingItems extends LootModifier {
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
						world.addEntity( new ExperienceOrbEntity( world, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D,
							experience
						) );
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

		private static class Serializer extends GlobalLootModifierSerializer< SmeltingItems > {
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

	private static class AddItemsDirectlyToInventory extends LootModifier {
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

		private static class Serializer extends GlobalLootModifierSerializer< AddItemsDirectlyToInventory > {
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
}
