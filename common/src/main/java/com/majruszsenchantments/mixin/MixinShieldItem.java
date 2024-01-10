package com.majruszsenchantments.mixin;

import com.majruszsenchantments.data.Config;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ShieldItem.class )
public abstract class MixinShieldItem extends MixinItem {
	@Override
	protected void getEnchantmentValue( CallbackInfoReturnable< Integer > callback ) {
		if( Config.IS_SHIELD_ENCHANTABLE ) {
			callback.setReturnValue( 1 );
		}
	}
}
