package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.mlib.Random;
import com.mlib.loot_modifiers.LootHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

/** Functionality of Harvest enchantment. */
public class Replant extends LootModifier {
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
		if( blockState == null || entity == null || hoe == null || origin == null || !isAtMaxAge( blockState ) )
			return generatedLoot;

		BlockPos position = new BlockPos( origin );
		/*int rangeFactor = Registries.HARVESTER.range.get() * EnchantmentHelper.getItemEnchantmentLevel( Registries.HARVESTER, hoe );

		removeSeedsFromLoot( generatedLoot, entity.level, blockState, position );
		if( entity.level instanceof ServerLevel && entity instanceof LivingEntity )
			tickInRange( rangeFactor, ( ServerLevel )entity.level, position, ( LivingEntity )entity, hoe );*/

		return generatedLoot;
	}

	/** Checks whether given block is at max age. */
	protected static boolean isAtMaxAge( BlockState blockState ) {
		Block block = blockState.getBlock();
		if( block instanceof CropBlock ) {
			CropBlock crops = ( CropBlock )block;

			return crops.isMaxAge( blockState );
		} else if( block instanceof NetherWartBlock ) {
			int netherWartMaximumAge = 3;

			return blockState.getValue( NetherWartBlock.AGE ) >= netherWartMaximumAge;
		}

		return false;
	}

	/** Removes one piece of seeds from loot if there are any. */
	protected static void removeSeedsFromLoot( List< ItemStack > generatedLoot, Level world, BlockState blockState, BlockPos position ) {
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

	/** Returns seed item depending on given block. */
	protected static Item getSeedItem( Level world, BlockState blockState, BlockPos position ) {
		Block block = blockState.getBlock();
		if( block instanceof CropBlock ) {
			ItemStack itemStack = ( ( CropBlock )block ).getCloneItemStack( world, position, blockState );
			return itemStack.getItem();
		} else if( block instanceof NetherWartBlock ) {
			ItemStack itemStack = ( ( NetherWartBlock )block ).getCloneItemStack( world, position, blockState );
			return itemStack.getItem();
		}

		return Items.STRUCTURE_BLOCK;
	}

	/** Increases nearby crops age and damages hoe. */
	protected static void tickInRange( int range, ServerLevel world, BlockPos position, LivingEntity entity, ItemStack hoe ) {
		/*for( int z = -range; z <= range; z++ )
			for( int x = -range; x <= range; x++ ) {
				if( x == 0 && z == 0 )
					continue;

				BlockPos neighbourPosition = new BlockPos( position.offset( x, 0, z ) );
				BlockState blockState = world.getBlockState( neighbourPosition );

				if( blockState.getBlock() instanceof CropBlock ) {
					CropBlock cropsBlock = ( CropBlock )blockState.getBlock();
					double growChance = Registries.HARVESTER.growChance.get();
					if( growChance > 0.0 ) {
						if( Random.tryChance( growChance ) ) {
							int penalty = Registries.HARVESTER.durabilityPenalty.get();
							cropsBlock.growCrops( world, neighbourPosition, blockState );
							spawnParticles( world, neighbourPosition, 3 );
							if( penalty > 0 )
								hoe.hurtAndBreak( penalty, entity, owner->owner.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
						}
						spawnParticles( world, neighbourPosition, 1 );
					}
				} else if( blockState.getBlock() instanceof NetherWartBlock ) {
					NetherWartBlock netherWartBlock = ( NetherWartBlock )blockState.getBlock();
					double growChance = Registries.HARVESTER.netherWartGrowChance.get();
					if( growChance > 0.0 ) {
						if( Random.tryChance( growChance ) ) {
							int penalty = Registries.HARVESTER.durabilityPenalty.get();
							int newAge = Mth.clamp( blockState.getValue( NetherWartBlock.AGE ) + 1, 0, 3 );
							world.setBlockAndUpdate( neighbourPosition, netherWartBlock.defaultBlockState()
								.setValue( NetherWartBlock.AGE, newAge ) );
							spawnParticles( world, neighbourPosition, 3 );
							if( penalty > 0 )
								hoe.hurtAndBreak( penalty, entity, owner->owner.broadcastBreakEvent( EquipmentSlot.MAINHAND ) );
						}
						spawnParticles( world, neighbourPosition, 1 );
					}
				}
			}*/
	}

	/** Spawning particles on nearby crops. */
	protected static void spawnParticles( ServerLevel world, BlockPos position, int amount ) {
		world.sendParticles( ParticleTypes.HAPPY_VILLAGER, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, amount, 0.25, 0.25,
			0.25, 0.1
		);
	}

	public static class Serializer extends GlobalLootModifierSerializer< Replant > {
		@Override
		public Replant read( ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn ) {
			return new Replant( conditionsIn );
		}

		@Override
		public JsonObject write( Replant instance ) {
			return null;
		}
	}
}

