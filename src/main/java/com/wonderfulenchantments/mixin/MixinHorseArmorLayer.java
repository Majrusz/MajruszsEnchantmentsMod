package com.wonderfulenchantments.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@OnlyIn( Dist.CLIENT )
@Mixin( HorseArmorLayer.class )
public abstract class MixinHorseArmorLayer {
	@Overwrite
	public void render( PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, Horse horse, float limbSwing, float limbSwingAmount,
		float partialTicks, float age, float headYaw, float headPitch
	) {
		HorseArmorLayer layer = ( HorseArmorLayer )( Object )this;
		ItemStack itemstack = horse.getArmor();
		if( itemstack.getItem() instanceof HorseArmorItem horseArmorItem ) {
			layer.getParentModel().copyPropertiesTo( layer.model );
			layer.model.prepareMobModel( horse, limbSwing, limbSwingAmount, partialTicks );
			layer.model.setupAnim( horse, limbSwing, limbSwingAmount, age, headYaw, headPitch );
			float f;
			float f1;
			float f2;
			if( horseArmorItem instanceof DyeableHorseArmorItem dyeableItem ) {
				int i = dyeableItem.getColor( itemstack );
				f = ( float )( i >> 16 & 255 ) / 255.0F;
				f1 = ( float )( i >> 8 & 255 ) / 255.0F;
				f2 = ( float )( i & 255 ) / 255.0F;
			} else {
				f = 1.0F;
				f1 = 1.0F;
				f2 = 1.0F;
			}

			VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer( multiBufferSource, RenderType.armorCutoutNoCull( horseArmorItem.getTexture() ), false, itemstack.isEnchanted() );
			layer.model.renderToBuffer( poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F );
		}
	}
}
