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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Functionality of Telekinesis enchantment. */
public class AddItemsDirectlyToInventory extends LootModifier {
	private final String TELEKINESIS_TAG = "TelekinesisLastTimeTag";

	public AddItemsDirectlyToInventory( ILootCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = context.get( LootParameters.TOOL );
		if( tool == null )
			return generatedLoot;

		Entity entity = context.get( LootParameters.THIS_ENTITY );
		if( !( entity instanceof PlayerEntity ) )
			return generatedLoot;

		PlayerEntity player = ( PlayerEntity )entity;
		if( !isTimeDifferentFromPreviousTelekinesisTick( player ) ) {
			generatedLoot.clear();
			return generatedLoot;
		}

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
		updateLastTelekinesisTime( player );

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
		data.putLong( TELEKINESIS_TAG, world.getDayTime() );
	}

	private boolean isTimeDifferentFromPreviousTelekinesisTick( PlayerEntity player ) {
		World world = player.getEntityWorld();
		CompoundNBT data = player.getPersistentData();

		return data.getLong( TELEKINESIS_TAG ) != world.getDayTime();
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
