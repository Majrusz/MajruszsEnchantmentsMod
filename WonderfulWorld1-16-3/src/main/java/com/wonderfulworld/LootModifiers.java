package com.wonderfulworld;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber( modid = WonderfulWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
    public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		event.getRegistry().register( new FishingItems.Serializer().setRegistryName( new ResourceLocation( WonderfulWorld.MOD_ID, "fishing_new_items" ) ) );
    }
	
	private static class FishingItems extends LootModifier {
		private static class ItemWithWeight {
			ItemWithWeight( Item item, int weight ) {
				this.item = item;
				this.weight = weight;
			}
			
			Item item;
			int weight;
		}
		
		private final Item replacement;
		private final List< ItemWithWeight > itemsToReplace;
		private final int sumOfWeights;
		
		public FishingItems( ILootCondition[] conditionsIn, Item replacement, List< ItemWithWeight > itemsToReplace ) {
			super( conditionsIn );
			
			this.replacement = replacement;
			this.itemsToReplace = itemsToReplace;

			int sum = 0;
			for( ItemWithWeight object : itemsToReplace )
				sum += object.weight;

			this.sumOfWeights = sum;
		}
	
		@Nonnull
		@Override
		public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
			int counter = 0;
			for( ItemStack stack : generatedLoot )
				if( stack.getItem() == this.replacement )
					counter += stack.getCount();
			
			if( counter > 0 ) {
				generatedLoot.removeIf( x -> x.getItem() == replacement );
				generatedLoot.add( new ItemStack( this.getRandomItem(), counter ) );
			}
			
			return generatedLoot;
		}
	
		private static class Serializer extends GlobalLootModifierSerializer< FishingItems > {
			@Override
			public FishingItems read( ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn ) {
				Item replacement = ForgeRegistries.ITEMS.getValue( new ResourceLocation( JSONUtils.getString( object, "replacement" ) ) );
				JsonArray items = JSONUtils.getJsonArray( object, "items" );

				List< ItemWithWeight > itemsToReplace = new ArrayList< ItemWithWeight >();
				for( int i = 0; i < items.size(); i++ ) {
					JsonObject item = items.get( i ).getAsJsonObject();

					itemsToReplace.add( new ItemWithWeight(
						ForgeRegistries.ITEMS.getValue( new ResourceLocation( item.get( "name" ).getAsString() ) ),
						item.get( "weight" ).getAsInt()
					) );
				}

				return new FishingItems( conditionsIn, replacement, itemsToReplace );
			}

			@Override
			public JsonObject write( FishingItems instance ) {
				return null;
			}
		}
		
		private Item getRandomItem() {
			int random = WonderfulWorld.RANDOM.nextInt( this.sumOfWeights );
			
			int current = 0;
			for( ItemWithWeight object : itemsToReplace ) {
				current += object.weight;
				if( current > random )
					return object.item;
			}
			
			return itemsToReplace.get( 0 ).item;
		}
	}
}
