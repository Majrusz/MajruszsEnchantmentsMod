package com.wonderfulenchantments.mixin;

import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ShieldItem.class )
public abstract class MixinShieldItem extends MixinItem {
	@Override
	protected void getEnchantmentValue( CallbackInfoReturnable< Integer > callback ) {
		callback.setReturnValue( 1 );
	}
}
