package com.wonderfulenchantments;

import com.wonderfulenchantments.enchantments.GottaMineFastEnchantment.GottaMineFastMultiplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/** Handling connection between server and client. */
public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( new ResourceLocation( "wonderful_enchantments", "main" ), ()->PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
		);
		CHANNEL.registerMessage( 0, GottaMineFastMultiplier.class, GottaMineFastMultiplier::encode, GottaMineFastMultiplier::new,
			GottaMineFastMultiplier::handle
		);
	}
}
