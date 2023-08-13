package com.majruszsenchantments;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/**
 Main class for the whole Wonderful Enchantments modification.

 @author Majrusz
 @since 2020-11-03 */
@Mod( MajruszsEnchantments.MOD_ID )
public class MajruszsEnchantments {
	public static final String MOD_ID = "majruszsenchantments";
	public static final String NAME = "Majrusz's Enchantments";

	public MajruszsEnchantments() {
		com.majruszsenchantments.Registries.initialize();
		MinecraftForge.EVENT_BUS.register( this );
	}
}