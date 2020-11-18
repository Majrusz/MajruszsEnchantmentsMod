package com.wonderfulworld;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber( modid = WonderfulWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class VillagerTrades {
	public static void register() {
		MinecraftForge.EVENT_BUS.register( new VillagerTrades() );
	}

	@SubscribeEvent
	public void addNewWandererTrades( WandererTradesEvent event ) {
		List< ITrade > trades = event.getRareTrades();

		trades.add( ( ITrade )new BasicTrade( new ItemStack( Items.EMERALD.asItem(), 5 ), new ItemStack( RegistryHandler.KREMOWKA.get(), 10 ), 6, 20, 0 ) );
	}

	@SubscribeEvent
	public void addNewTrades( VillagerTradesEvent event ) {
		if( event.getType() == VillagerProfession.FISHERMAN )
			this.addNewFishermanTrade( event );

		if( event.getType() == VillagerProfession.FARMER )
			this.addNewFarmerTrade( event );
	}

	private void addNewFishermanTrade( VillagerTradesEvent event ) {
		List< ITrade > trades_novice = event.getTrades().get( 1 );

		trades_novice.add( ( ITrade )new BasicTrade( new ItemStack( RegistryHandler.TIN.get(), 3 ), new ItemStack( Items.EMERALD.asItem(), 1 ), 16, 10, 0.05f ) );

		List< ITrade > trades_journeyman = event.getTrades().get( 3 );

		trades_journeyman.add( ( ITrade )new BasicTrade( new ItemStack( RegistryHandler.BASS.get(), 13 ), new ItemStack( Items.EMERALD.asItem(), 1 ), 16, 15, 0.05f ) );

		List< ITrade > trades_expert = event.getTrades().get( 4 );

		trades_expert.add( ( ITrade )new BasicTrade( new ItemStack( RegistryHandler.HERRING.get(), 8 ), new ItemStack( Items.EMERALD.asItem(), 1 ), 12, 20, 0.05f ) );

		trades_expert.add( ( ITrade )new BasicTrade( new ItemStack( RegistryHandler.HERRING.get(), 10 ), new ItemStack( Items.EMERALD.asItem(), 2 ), new ItemStack( RegistryHandler.SURSTROMMING.get(), 2 ), 16, 20, 0.05f ) );
	}

	private void addNewFarmerTrade( VillagerTradesEvent event ) {
		List< ITrade > trades_novice = event.getTrades().get( 1 );

		trades_novice.add( ( ITrade )new BasicTrade( new ItemStack( RegistryHandler.SALT.get(), 24 ), new ItemStack( Items.EMERALD.asItem(), 1 ), 16, 1, 0.05f ) );
	}
}
