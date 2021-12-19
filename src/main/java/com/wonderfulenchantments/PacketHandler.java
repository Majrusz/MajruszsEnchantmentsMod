package com.wonderfulenchantments;

import com.wonderfulenchantments.enchantments.GottaMineFastEnchantment.MultiplierMessage;
import com.wonderfulenchantments.enchantments.SixthSenseEnchantment.VelocityMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Handling connection between server and client. */
public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	public static void registerPacket( final FMLCommonSetupEvent event ) {
		CHANNEL = NetworkRegistry.newSimpleChannel( new ResourceLocation( "wonderful_enchantments", "main" ), ()->PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
		);
		CHANNEL.registerMessage( 0, MultiplierMessage.class, MultiplierMessage::encode, MultiplierMessage::new, MultiplierMessage::handle );
		CHANNEL.registerMessage( 1, VelocityMessage.class, VelocityMessage::encode, VelocityMessage::new, VelocityMessage::handle );
	}
}
