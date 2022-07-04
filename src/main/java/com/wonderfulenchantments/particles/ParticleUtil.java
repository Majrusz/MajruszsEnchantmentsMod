package com.wonderfulenchantments.particles;

import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@SubscribeEvent
	public static void registerParticles( ParticleFactoryRegisterEvent event ) {
		Minecraft.getInstance().particleEngine.register( Registries.DODGE_PARTICLE.get(), DodgeParticle.Factory::new );
	}
}
