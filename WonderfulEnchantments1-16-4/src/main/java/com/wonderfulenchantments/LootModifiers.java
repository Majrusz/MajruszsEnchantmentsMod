package com.wonderfulenchantments;

import com.wonderfulenchantments.loot_modifiers.AddItemsDirectlyToInventory;
import com.wonderfulenchantments.loot_modifiers.Replant;
import com.wonderfulenchantments.loot_modifiers.SmeltingItems;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

/** Loot modifiers responsible for adding, removing or chaning loot. */
@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		IForgeRegistry< GlobalLootModifierSerializer< ? > > registry = event.getRegistry();

		registerSingleModifier( registry, new SmeltingItems.Serializer(), "smelter_enchantment" );
		registerSingleModifier( registry, new AddItemsDirectlyToInventory.Serializer(), "telekinesis_enchantment" );
		registerSingleModifier( registry, new Replant.Serializer(), "harvester_enchantment" );
	}

	/** Adding to registry single loot modifier. */
	protected static void registerSingleModifier( IForgeRegistry< GlobalLootModifierSerializer< ? > > registry, GlobalLootModifierSerializer< ? > serializer, String registerName ) {
		registry.register( serializer.setRegistryName( WonderfulEnchantments.getLocation( registerName ) ) );
	}
}
