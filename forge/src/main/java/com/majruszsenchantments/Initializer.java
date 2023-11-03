package com.majruszsenchantments;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod( MajruszsEnchantments.MOD_ID )
public class Initializer {
	public Initializer() {
		MajruszsEnchantments.HELPER.register();
		MinecraftForge.EVENT_BUS.register( this );
	}
}
