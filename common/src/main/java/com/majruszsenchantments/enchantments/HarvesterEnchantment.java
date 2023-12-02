package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnPlayerInteracted;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.level.BlockHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.platform.Side;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

@AutoInstance
public class HarvesterEnchantment extends Handler {
	float growChance = 0.04f;

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
		this.collectCrop( data );
		if( Side.isLogicalServer() ) {
			this.spawnItems( data, this.tryToReplant( data ) );
			this.tickNearbyCrops( data );
			this.damageHoe( data );
		}
		data.finish();
	}

	private void collectCrop( OnPlayerInteracted data ) {
		BlockPos blockPos = data.blockResult.getBlockPos();
		BlockState blockState = data.getLevel().getBlockState( blockPos );
		blockState.getBlock().playerWillDestroy( data.getLevel(), blockPos, blockState, data.player );
	}

	private void spawnItems( OnPlayerInteracted data, List< ItemStack > itemStacks ) {
		itemStacks.forEach( itemStack->Block.popResource( data.getLevel(), data.blockResult.getBlockPos(), itemStack ) );
	}

	private List< ItemStack > tryToReplant( OnPlayerInteracted data ) {
		Level level = data.getLevel();
		BlockPos blockPos = data.blockResult.getBlockPos();
		BlockState blockState = level.getBlockState( blockPos );
		Block block = blockState.getBlock();
		Item seed = block.getCloneItemStack( level, blockPos, blockState ).getItem();
		List< ItemStack > itemStacks = blockState.getDrops( new LootParams.Builder( data.getServerLevel() )
			.withParameter( LootContextParams.ORIGIN, AnyPos.from( blockPos ).center().vec3() )
			.withParameter( LootContextParams.TOOL, data.itemStack )
			.withParameter( LootContextParams.BLOCK_STATE, blockState )
			.withParameter( LootContextParams.THIS_ENTITY, data.player )
		);
		for( ItemStack itemStack : itemStacks ) {
			if( itemStack.is( seed ) ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				level.setBlockAndUpdate( blockPos, block.defaultBlockState() );
				return itemStacks;
			}
		}
		for( Slot slot : data.player.inventoryMenu.slots ) {
			ItemStack itemStack = slot.getItem();
			if( itemStack.is( seed ) ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				level.setBlockAndUpdate( blockPos, block.defaultBlockState() );
				return itemStacks;
			}
		}

		level.setBlockAndUpdate( blockPos, Blocks.AIR.defaultBlockState() );
		return itemStacks;
	}

	private void tickNearbyCrops( OnPlayerInteracted data ) {
		BlockPos blockPos = data.blockResult.getBlockPos();
		int range = EnchantmentHelper.getLevel( this.enchantment, data.itemStack );
		for( int z = -range; z <= range; ++z ) {
			for( int x = -range; x <= range; ++x ) {
				if( x == 0 && z == 0 ) {
					continue;
				}

				BlockPos neighbourPosition = blockPos.offset( x, 0, z );
				BlockState blockState = data.getLevel().getBlockState( blockPos.offset( x, 0, z ) );
				Block block = blockState.getBlock();
				if( !( block instanceof CropBlock ) && !( block instanceof NetherWartBlock ) ) {
					continue;
				}

				int particlesCount = 1;
				if( Random.check( this.growChance ) ) {
					BlockHelper.growCrop( data.getLevel(), neighbourPosition );
					particlesCount = 3;
				}

				ParticleEmitter.of( ParticleTypes.HAPPY_VILLAGER )
					.position( AnyPos.from( blockPos ).center().vec3() )
					.count( particlesCount )
					.emit( data.getServerLevel() );
			}
		}
	}

	private void damageHoe( OnPlayerInteracted data ) {
		ItemHelper.damage( data.player, data.hand, 1 );
	}
}
