package com.wonderfulworld.blocks;

import com.wonderfulworld.RegistryHandler;
import com.wonderfulworld.WonderfulWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class NetherSaltOre extends OreBlock {
	public NetherSaltOre() {
		super( Properties.create( Material.ROCK )
			.hardnessAndResistance( 2.0f, 2.0f )
			.sound( SoundType.STONE )
			.harvestTool( ToolType.PICKAXE )
			.harvestLevel( 1 )
			.lootFrom( RegistryHandler.SALT_ORE.get() )
		);
	}

	@Override
	public int getExpDrop( BlockState state, IWorldReader reader, BlockPos position, int fortuneLevel, int silkTouchLevel ) {
		return ( silkTouchLevel > 0 ) ? 0 : MathHelper.nextInt( WonderfulWorld.RANDOM, 0, 2 );
	}
}
