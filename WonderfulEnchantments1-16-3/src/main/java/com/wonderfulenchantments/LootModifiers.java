package com.wonderfulenchantments;

import com.wonderfulenchantments.loot_modifiers.AddItemsDirectlyToInventory;
import com.wonderfulenchantments.loot_modifiers.SmeltingItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class LootModifiers {
	@SubscribeEvent
	public static void registerModifierSerializers( final RegistryEvent.Register< GlobalLootModifierSerializer< ? > > event ) {
		IForgeRegistry< GlobalLootModifierSerializer< ? > > registry = event.getRegistry();

		registry.register(
			new SmeltingItems.Serializer().setRegistryName( new ResourceLocation( WonderfulEnchantments.MOD_ID, "smelter_enchantment" ) ) );
		registry.register( new AddItemsDirectlyToInventory.Serializer().setRegistryName(
			new ResourceLocation( WonderfulEnchantments.MOD_ID, "telekinesis_enchantment" ) ) );
	}
}
