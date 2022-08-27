package com.majruszsenchantments.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn( Dist.CLIENT )
@Mixin( HorseArmorLayer.class )
public abstract class MixinHorseArmorLayer {
	@ModifyVariable( method = "render", at = @At( "STORE" ), ordinal = 0 )
	public VertexConsumer render( VertexConsumer consumer, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, Horse horse ) {
		ItemStack itemStack = horse.getArmor();
		ResourceLocation texture = ( ( HorseArmorItem )itemStack.getItem() ).getTexture();
		return ItemRenderer.getArmorFoilBuffer( multiBufferSource, RenderType.armorCutoutNoCull( texture ), false, itemStack.isEnchanted() );
	}
}
