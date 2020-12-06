package com.wonderfulenchantments;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		event.getRegistry()
			.register(
				new SmeltingItems.Serializer().setRegistryName( new ResourceLocation( WonderfulEnchantments.MOD_ID, "smelter_enchantment" ) ) );
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

			int smelterLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SMELTER.get(), tool );
			ServerWorld world = context.getWorld();

			ArrayList< ItemStack > output = new ArrayList<>();
			for( ItemStack itemStack : generatedLoot ) {
				if( WonderfulEnchantments.RANDOM.nextDouble() <= getSmeltChance( smelterLevel ) ) {
					output.add( smelt( itemStack, context ) );
					WonderfulEnchantments.LOGGER.info( "!" );
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
							1 + WonderfulEnchantments.RANDOM.nextInt( 3 ), 0.125D, 0.125D, 0.125D, 0.03125D
						);
					}
				} else
					output.add( itemStack );
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

		protected static double getSmeltChance( int smelterLevel ) {
			switch( smelterLevel ) {
				case 1:
					return 0.25D;
				case 2:
					return 0.5D;
				case 3:
					return 1.0D;
				default:
					return 0.0D;
			}
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
}
