package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnPlayerInteracted;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.level.BlockHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.platform.Side;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoInstance
public class HarvesterEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( MajruszsEnchantments.IS_HOE )
			.slots( EquipmentSlots.HANDS )
			.maxLevel( 3 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 15 );
	}

	public HarvesterEnchantment() {
		super( MajruszsEnchantments.HARVESTER, HarvesterEnchantment.class, false );

		OnPlayerInteracted.listen( this::apply )
			.addCondition( data->data.blockResult != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.itemStack ) )
			.addCondition( data->BlockHelper.isCropAtMaxAge( data.getLevel(), data.blockResult.getBlockPos() ) );

		// TODO: 3x3 till
	}

	private void apply( OnPlayerInteracted data ) {
		this.collectNearbyCrops( data );
		if( Side.isLogicalServer() ) {
			Result result = this.harvestNearbyCrops( data );
			this.spawnItems( data, result );
			this.damageHoe( data, result );
		}
		data.finish();
	}

	private void collectNearbyCrops( OnPlayerInteracted data ) {
		int range = EnchantmentHelper.getLevel( this.enchantment, data.itemStack ) - 1;
		for( int z = -range; z <= range; ++z ) {
			for( int x = -range; x <= range; ++x ) {
				BlockPos blockPos = AnyPos.from( data.blockResult.getBlockPos() ).add( x, 0, z ).block();
				BlockState blockState = data.getLevel().getBlockState( blockPos );
				if( BlockHelper.isCropAtMaxAge( blockState ) ) {
					blockState.getBlock().playerWillDestroy( data.getLevel(), blockPos, blockState, data.player );
				}
			}
		}
	}

	private void spawnItems( OnPlayerInteracted data, Result result ) {
		result.itemStacks.forEach( itemStack->Block.popResource( data.getLevel(), data.blockResult.getBlockPos(), itemStack ) );
	}

	private Result harvestNearbyCrops( OnPlayerInteracted data ) {
		List< ItemStack > itemStacks = new ArrayList<>();
		int count = 0;
		int range = EnchantmentHelper.getLevel( this.enchantment, data.itemStack ) - 1;
		for( int z = -range; z <= range; ++z ) {
			for( int x = -range; x <= range; ++x ) {
				Optional< List< ItemStack > > drops = this.tryToHarvest( data, x, z );
				if( drops.isPresent() ) {
					++count;
					itemStacks.addAll( drops.get() );
				}
			}
		}

		return new Result( itemStacks, count );
	}

	private Optional< List< ItemStack > > tryToHarvest( OnPlayerInteracted data, int x, int z ) {
		Level level = data.getLevel();
		BlockPos blockPos = AnyPos.from( data.blockResult.getBlockPos() ).add( x, 0, z ).block();
		BlockState blockState = level.getBlockState( blockPos );
		if( !BlockHelper.isCropAtMaxAge( blockState ) ) {
			return Optional.empty();
		}
		Block block = blockState.getBlock();
		Item seed = block.getCloneItemStack( level, blockPos, blockState ).getItem();
		List< ItemStack > drops = blockState.getDrops( new LootParams.Builder( data.getServerLevel() )
			.withParameter( LootContextParams.ORIGIN, AnyPos.from( blockPos ).center().vec3() )
			.withParameter( LootContextParams.TOOL, data.itemStack )
			.withParameter( LootContextParams.BLOCK_STATE, blockState )
			.withParameter( LootContextParams.THIS_ENTITY, data.player )
		);
		for( ItemStack itemStack : drops ) {
			if( itemStack.is( seed ) ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				level.setBlockAndUpdate( blockPos, block.defaultBlockState() );
				return Optional.of( drops );
			}
		}
		for( Slot slot : data.player.inventoryMenu.slots ) {
			ItemStack itemStack = slot.getItem();
			if( itemStack.is( seed ) ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				level.setBlockAndUpdate( blockPos, block.defaultBlockState() );
				return Optional.of( drops );
			}
		}

		level.setBlockAndUpdate( blockPos, Blocks.AIR.defaultBlockState() );
		return Optional.of( drops );
	}

	private void damageHoe( OnPlayerInteracted data, Result result ) {
		ItemHelper.damage( data.player, data.hand, result.cropsCount );
	}

	private record Result( List< ItemStack > itemStacks, int cropsCount ) {}
}
