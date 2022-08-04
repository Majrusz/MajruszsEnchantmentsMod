package com.majruszsenchantments.particles;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.Registries;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = MajruszsEnchantments.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ParticleUtil {
	@OnlyIn( Dist.CLIENT )
	@SubscribeEvent
	public static void registerParticles( ParticleFactoryRegisterEvent event ) {
		Minecraft.getInstance().particleEngine.register( Registries.DODGE_PARTICLE.get(), DodgeParticle.Factory::new );
	}
}
