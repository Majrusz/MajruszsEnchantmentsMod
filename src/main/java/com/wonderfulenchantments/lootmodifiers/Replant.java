package com.wonderfulenchantments.lootmodifiers;

import com.google.common.base.Suppliers;
import com.mlib.blocks.BlockHelper;
import com.mlib.loot_modifiers.LootHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class Replant extends LootModifier {
	public static final Supplier< Codec< Replant > > CODEC = Suppliers.memoize( ()->RecordCodecBuilder.create( inst->codecStart( inst ).apply( inst, Replant::new ) ) );

	public Replant( LootItemCondition[] conditionsIn ) {
		super( conditionsIn );
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		BlockState blockState = LootHelper.getParameter( context, LootContextParams.BLOCK_STATE );
		Entity entity = LootHelper.getParameter( context, LootContextParams.THIS_ENTITY );
		ItemStack hoe = LootHelper.getParameter( context, LootContextParams.TOOL );
		Vec3 origin = LootHelper.getParameter( context, LootContextParams.ORIGIN );
		if( blockState == null || entity == null || hoe == null || origin == null || !BlockHelper.isCropAtMaxAge( entity.level, new BlockPos( origin ) ) )
			return generatedLoot;

		removeSeedsFromLoot( generatedLoot, entity.level, blockState, new BlockPos( origin ) );
		return generatedLoot;
	}

	@Override
	public Codec< ? extends IGlobalLootModifier > codec() {
		return CODEC.get();
	}

	private static void removeSeedsFromLoot( List< ItemStack > generatedLoot, Level world, BlockState blockState, BlockPos position ) {
		Block block = blockState.getBlock();
		Item seedItem = getSeedItem( world, blockState, position );
		for( ItemStack itemStack : generatedLoot ) {
			if( itemStack.getItem() == seedItem ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				world.setBlockAndUpdate( position, block.defaultBlockState() );
				return;
			}
		}

		world.setBlockAndUpdate( position, Blocks.AIR.defaultBlockState() );
	}

	private static Item getSeedItem( Level level, BlockState blockState, BlockPos position ) {
		return blockState.getBlock().getCloneItemStack( level, position, blockState ).getItem();
	}
}

