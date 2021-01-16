package com.wonderfulenchantments;

import com.wonderfulenchantments.renderers.HorseRendererReplacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static void replaceStandardMinecraftHorseArmorLayer() {
		EntityRendererManager rendererManager = Minecraft.getInstance()
			.getRenderManager();
		rendererManager.register( EntityType.HORSE, new HorseRendererReplacement( rendererManager ) );
	}
}
