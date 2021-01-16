package com.wonderfulenchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class WonderfulEnchantmentHelper {
	public static boolean isDirectDamageFromLivingEntity( DamageSource source ) {
		return source.getTrueSource() instanceof LivingEntity && source.getImmediateSource() instanceof LivingEntity;
	}
}
