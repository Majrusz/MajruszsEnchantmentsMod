package com.majruszsenchantments.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Item.class )
public abstract class MixinItem {
	@Inject(
		at = @At( "RETURN" ),
		cancellable = true,
		method = "getEnchantmentValue ()I"
	)
	protected void getEnchantmentValue( CallbackInfoReturnable< Integer > callback ) {}

	@Inject(
		at = @At( "RETURN" ),
		cancellable = true,
		method = "isEnchantable (Lnet/minecraft/world/item/ItemStack;)Z"
	)
	protected void isEnchantable( ItemStack itemStack, CallbackInfoReturnable< Boolean > callback ) {}
}
