package com.wonderfulenchantments;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class WonderfulEnchantmentHelper {
	public static boolean isDirectDamageFromLivingEntity( DamageSource source ) {
		return source.getTrueSource() instanceof LivingEntity && source.getImmediateSource() instanceof LivingEntity;
	}

	/** Checks whether entity is outside when it is raining. */
	public static boolean isEntityOutsideWhenItRains( LivingEntity entity, ServerWorld world ) {
		BlockPos entityPosition = new BlockPos( entity.getPositionVec() );
		Biome biome = world.getBiome( entityPosition );

		return world.canSeeSky( entityPosition ) && world.isRaining() && biome.getPrecipitation() == Biome.RainType.RAIN;
	}
}
