package com.wonderfulenchantments.renderers;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class HorseRendererReplacement extends AbstractHorseRenderer< HorseEntity, HorseModel< HorseEntity > > {
	private static final Map< String, ResourceLocation > LAYERED_LOCATION_CACHE = Maps.newHashMap();

	public HorseRendererReplacement( EntityRendererManager renderManagerIn ) {
		super( renderManagerIn, new HorseModel<>( 0.0F ), 1.1F );
		this.layerRenderers.clear();
		this.addLayer( new HorseArmorLayerReplacement( this ) );
	}

	public ResourceLocation getEntityTexture( HorseEntity entity ) {
		String s = entity.getHorseTexture();
		ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get( s );
		if( resourcelocation == null ) {
			resourcelocation = new ResourceLocation( s );
			Minecraft.getInstance()
				.getTextureManager()
				.loadTexture( resourcelocation, new LayeredTexture( entity.getVariantTexturePaths() ) );
			LAYERED_LOCATION_CACHE.put( s, resourcelocation );
		}

		return resourcelocation;
	}

}
