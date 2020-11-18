package com.wonderfulworld.blocks;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class NetherIronOre extends OreBlock {
	public NetherIronOre() {
		super( Properties.create( Material.ROCK )
			.hardnessAndResistance( 3.0f, 3.0f )
			.sound( SoundType.STONE )
			.harvestTool( ToolType.PICKAXE )
			.harvestLevel( 1 )
		);
	}

	@Override
	public int getExpDrop( BlockState state, IWorldReader reader, BlockPos position, int fortuneLevel, int silkTouchLevel ) {
		return ( silkTouchLevel > 0 ) ? 0 : MathHelper.nextInt( WonderfulWorld.RANDOM, 1, 4 );
	}
}
