package com.wonderfulenchantments.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Replacement for Horse Armor that makes enchantments effect visible on it. */
@OnlyIn( Dist.CLIENT )
public class HorseArmorLayerReplacement extends RenderLayer< Horse, HorseModel< Horse > > {
	private final HorseModel< Horse > model;

	public HorseArmorLayerReplacement( RenderLayerParent< Horse, HorseModel< Horse > > parent, EntityModelSet entityModelSet ) {
		super( parent );
		this.model = new HorseModel<>( entityModelSet.bakeLayer( ModelLayers.HORSE_ARMOR ) );
	}

	public void render( PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, Horse horse, float limbSwing, float limbSwingAmount,
		float partialTicks, float age, float headYaw, float headPitch
	) {
		ItemStack itemstack = horse.getArmor();
		if( itemstack.getItem() instanceof HorseArmorItem ) {
			HorseArmorItem horsearmoritem = ( HorseArmorItem )itemstack.getItem();
			this.getParentModel().copyPropertiesTo( this.model );
			this.model.prepareMobModel( horse, limbSwing, limbSwingAmount, partialTicks );
			this.model.setupAnim( horse, limbSwing, limbSwingAmount, age, headYaw, headPitch );
			float f;
			float f1;
			float f2;
			if( horsearmoritem instanceof DyeableHorseArmorItem ) {
				int i = ( ( DyeableHorseArmorItem )horsearmoritem ).getColor( itemstack );
				f = ( float )( i >> 16 & 255 ) / 255.0F;
				f1 = ( float )( i >> 8 & 255 ) / 255.0F;
				f2 = ( float )( i & 255 ) / 255.0F;
			} else {
				f = 1.0F;
				f1 = 1.0F;
				f2 = 1.0F;
			}

			VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer( multiBufferSource, RenderType.entityCutoutNoCull( horsearmoritem.getTexture() ), false, itemstack.isEnchanted() );
			this.model.renderToBuffer( poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F );
		}
	}
}
