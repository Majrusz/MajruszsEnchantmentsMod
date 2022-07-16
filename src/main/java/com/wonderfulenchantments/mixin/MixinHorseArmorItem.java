package com.wonderfulenchantments.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ShieldItem.class )
public abstract class MixinHorseArmorItem extends MixinItem {
	@Override
	protected void getEnchantmentValue( CallbackInfoReturnable< Integer > callback ) {
		callback.setReturnValue( 1 );
	}

	@Override
	protected void isEnchantable( ItemStack itemStack, CallbackInfoReturnable< Boolean > callback ) {
		callback.setReturnValue( itemStack.getMaxStackSize() == 1 );
	}
}
