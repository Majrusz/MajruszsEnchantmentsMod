package com.wonderfulworld.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class KremowkaBlock extends Block {
	public KremowkaBlock() {
		super( Properties.create( Material.PLANTS )
			.hardnessAndResistance( 0.5f, 1.0f )
			.sound( SoundType.PLANT )
		);
	}
}
