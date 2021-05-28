package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.wonderfulenchantments.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Functionality of Telekinesis enchantment. */
public class AddItemsDirectlyToInventory extends LootModifier {
	private final String TELEKINESIS_TIME_TAG = "TelekinesisLastTimeTag";
	private final String TELEKINESIS_POSITION_TAG = "TelekinesisLastPositionTag";

	public AddItemsDirectlyToInventory( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = context.get( LootParameters.TOOL );
		Vector3d position = context.get( LootParameters.field_237457_g_ );
		Entity entity = context.get( LootParameters.THIS_ENTITY );
		if( tool == null || position == null || !( entity instanceof PlayerEntity ) )
			return generatedLoot;

		PlayerEntity player = ( PlayerEntity )entity;
		if( isSameTimeAsPreviousTelekinesisTick( player ) && isSamePosition( player, position ) ) {
			generatedLoot.clear();
			return generatedLoot;
		}
		updateLastTelekinesisTime( player );
		updateLastBlockPosition( player, position );

		int harvesterLevel = EnchantmentHelper.getEnchantmentLevel( Instances.HARVESTER, player.getHeldItemMainhand() );
		Item seedItem = getSeedItem( entity.world, context.get( LootParameters.field_237457_g_ ), context.get( LootParameters.BLOCK_STATE ) );
		ArrayList< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : generatedLoot ) {
			if( harvesterLevel > 0 && itemStack.getItem() == seedItem ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				boolean didGivingItemSucceeded = player.inventory.addItemStackToInventory( itemStack );

				output.add( new ItemStack( seedItem, ( !didGivingItemSucceeded ? itemStack.getCount() : 0 ) + 1 ) );
			} else if( !player.inventory.addItemStackToInventory( itemStack ) ) {
				output.add( itemStack );
			}
		}

		return output;
	}

	@Nullable
	private Item getSeedItem( World world, Vector3d position, BlockState blockState ) {
		if( blockState == null || position == null )
			return null;

		if( !( blockState.getBlock() instanceof CropsBlock ) )
			return null;

		CropsBlock crops = ( CropsBlock )blockState.getBlock();
		ItemStack seeds = crops.getItem( world, new BlockPos( position ), blockState );
		return seeds.getItem();
	}

	private void updateLastTelekinesisTime( PlayerEntity player ) {
		World world = player.getEntityWorld();
		CompoundNBT data = player.getPersistentData();
		data.putLong( TELEKINESIS_TIME_TAG, world.getDayTime() );
	}

	private boolean isSameTimeAsPreviousTelekinesisTick( PlayerEntity player ) {
		World world = player.getEntityWorld();
		CompoundNBT data = player.getPersistentData();

		return data.getLong( TELEKINESIS_TIME_TAG ) == world.getDayTime();
	}

	private void updateLastBlockPosition( PlayerEntity player, Vector3d position ) {
		CompoundNBT data = player.getPersistentData();
		data.putString( TELEKINESIS_POSITION_TAG, position.toString() );
	}

	private boolean isSamePosition( PlayerEntity player, Vector3d position ) {
		CompoundNBT data = player.getPersistentData();

		return data.getString( TELEKINESIS_POSITION_TAG )
			.equals( position.toString() );
	}

	public static class Serializer extends GlobalLootModifierSerializer< AddItemsDirectlyToInventory > {
		@Override
		public AddItemsDirectlyToInventory read( ResourceLocation name, JsonObject object, ILootCondition[] conditions ) {
			return new AddItemsDirectlyToInventory( conditions );
		}

		@Override
		public JsonObject write( AddItemsDirectlyToInventory instance ) {
			return null;
		}
	}
}
