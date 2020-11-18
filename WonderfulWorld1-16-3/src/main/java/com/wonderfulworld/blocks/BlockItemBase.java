package com.wonderfulworld.blocks;

import com.wonderfulworld.WonderfulWorld;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockItemBase extends BlockItem {
	public BlockItemBase( Block block ) {
		super( block, new Properties().group( WonderfulWorld.TAB_BLOCKS ) );
	}
}
