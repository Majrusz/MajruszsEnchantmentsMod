package com.wonderfulenchantments.lootmodifiers;

import com.google.common.base.Suppliers;
import com.mlib.loot_modifiers.LootHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Functionality of Telekinesis enchantment. */
public class AddItemsDirectlyToInventory extends LootModifier {
	public static final Supplier< Codec< AddItemsDirectlyToInventory > > CODEC = Suppliers.memoize( ()->RecordCodecBuilder.create( inst->codecStart( inst ).apply( inst, AddItemsDirectlyToInventory::new ) ) );

	private final String TELEKINESIS_TIME_TAG = "TelekinesisLastTimeTag";
	private final String TELEKINESIS_POSITION_TAG = "TelekinesisLastPositionTag";

	public AddItemsDirectlyToInventory( LootItemCondition[] conditions ) {
		super( conditions );
	}

	@Nonnull
	@Override
	public ObjectArrayList< ItemStack > doApply( ObjectArrayList< ItemStack > generatedLoot, LootContext context ) {
		ItemStack tool = LootHelper.getParameter( context, LootContextParams.TOOL );
		Vec3 position = LootHelper.getParameter( context, LootContextParams.ORIGIN );
		Entity entity = LootHelper.getParameter( context, LootContextParams.THIS_ENTITY );
		if( tool == null || position == null || !( entity instanceof Player ) )
			return generatedLoot;

		Player player = ( Player )entity;
		if( isSameTimeAsPreviousTelekinesisTick( player ) && isSamePosition( player, position ) ) {
			generatedLoot.clear();
			return generatedLoot;
		}
		updateLastTelekinesisTime( player );
		updateLastBlockPosition( player, position );

		ObjectArrayList< ItemStack > output = new ObjectArrayList<>();
		/*int harvesterLevel = EnchantmentHelper.getItemEnchantmentLevel( Registries.HARVESTER, player.getMainHandItem() );
		Item seedItem = getSeedItem( entity.level, LootHelper.getParameter( context, LootContextParams.ORIGIN ), LootHelper.getParameter( context, LootContextParams.BLOCK_STATE ) );
		for( ItemStack itemStack : generatedLoot ) {
			if( harvesterLevel > 0 && itemStack.getItem() == seedItem ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				boolean didGivingItemSucceeded = player.getInventory().add( itemStack );

				output.add( new ItemStack( seedItem, ( !didGivingItemSucceeded ? itemStack.getCount() : 0 ) + 1 ) );
			} else if( !player.getInventory().add( itemStack ) ) {
				output.add( itemStack );
			}
		}*/

		return output;
	}

	@Override
	public Codec< ? extends IGlobalLootModifier > codec() {
		return CODEC.get();
	}

	@Nullable
	private Item getSeedItem( Level world, Vec3 position, BlockState blockState ) {
		if( blockState == null || position == null )
			return null;

		if( !( blockState.getBlock() instanceof CropBlock ) )
			return null;

		CropBlock crops = ( CropBlock )blockState.getBlock();
		ItemStack seeds = crops.getCloneItemStack( world, new BlockPos( position ), blockState );
		return seeds.getItem();
	}

	private void updateLastTelekinesisTime( Player player ) {
		Level world = player.level;
		CompoundTag data = player.getPersistentData();
		data.putLong( TELEKINESIS_TIME_TAG, world.getDayTime() );
	}

	private boolean isSameTimeAsPreviousTelekinesisTick( Player player ) {
		Level world = player.level;
		CompoundTag data = player.getPersistentData();

		return data.getLong( TELEKINESIS_TIME_TAG ) == world.getDayTime();
	}

	private void updateLastBlockPosition( Player player, Vec3 position ) {
		CompoundTag data = player.getPersistentData();
		data.putString( TELEKINESIS_POSITION_TAG, position.toString() );
	}

	private boolean isSamePosition( Player player, Vec3 position ) {
		CompoundTag data = player.getPersistentData();

		return data.getString( TELEKINESIS_POSITION_TAG ).equals( position.toString() );
	}
}
