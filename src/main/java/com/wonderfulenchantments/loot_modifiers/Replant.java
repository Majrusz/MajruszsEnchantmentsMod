package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.wonderfulenchantments.Instances;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

/** Functionality of Harvest enchantment. */
public class Replant extends LootModifier {
	public Replant( ILootCondition[] conditionsIn ) {
		super( conditionsIn );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		BlockState blockState = context.get( LootParameters.BLOCK_STATE );
		Entity entity = context.get( LootParameters.THIS_ENTITY );
		ItemStack hoe = context.get( LootParameters.TOOL );
		Vector3d origin = context.get( LootParameters.field_237457_g_ );
		if( blockState == null || entity == null || hoe == null || origin == null || !isAtMaxAge( blockState ) )
			return generatedLoot;

		BlockPos position = new BlockPos( origin );
		int rangeFactor = Instances.HARVESTER.range.get() * EnchantmentHelper.getEnchantmentLevel( Instances.HARVESTER, hoe );

		removeSeedsFromLoot( generatedLoot, entity.world, blockState, position );
		if( entity.world instanceof ServerWorld && entity instanceof LivingEntity )
			tickInRange( rangeFactor, ( ServerWorld )entity.world, position, ( LivingEntity )entity, hoe );

		return generatedLoot;
	}

	/** Checks whether given block is at max age. */
	protected static boolean isAtMaxAge( BlockState blockState ) {
		Block block = blockState.getBlock();
		if( block instanceof CropsBlock ) {
			CropsBlock crops = ( CropsBlock )block;

			return crops.isMaxAge( blockState );
		} else if( block instanceof NetherWartBlock ) {
			int netherWartMaximumAge = 3;

			return blockState.get( NetherWartBlock.AGE ) >= netherWartMaximumAge;
		}

		return false;
	}

	/** Removes one piece of seeds from loot if there are any. */
	protected static void removeSeedsFromLoot( List< ItemStack > generatedLoot, World world, BlockState blockState, BlockPos position ) {
		Block block = blockState.getBlock();
		Item seedItem = getSeedItem( world, blockState, position );
		for( ItemStack itemStack : generatedLoot ) {
			if( itemStack.getItem() == seedItem ) {
				itemStack.setCount( itemStack.getCount() - 1 );
				world.setBlockState( position, block.getDefaultState() );
				return;
			}
		}

		world.setBlockState( position, Blocks.AIR.getDefaultState() );
	}

	/** Returns seed item depending on given block. */
	protected static Item getSeedItem( World world, BlockState blockState, BlockPos position ) {
		Block block = blockState.getBlock();
		if( block instanceof CropsBlock ) {
			ItemStack itemStack = ( ( CropsBlock )block ).getItem( world, position, blockState );
			return itemStack.getItem();
		} else if( block instanceof NetherWartBlock ) {
			ItemStack itemStack = ( ( NetherWartBlock )block ).getItem( world, position, blockState );
			return itemStack.getItem();
		}

		return Items.STRUCTURE_BLOCK;
	}

	/** Increases nearby crops age and damages hoe. */
	protected static void tickInRange( int range, ServerWorld world, BlockPos position, LivingEntity entity, ItemStack hoe ) {
		for( int z = -range; z <= range; z++ )
			for( int x = -range; x <= range; x++ ) {
				if( x == 0 && z == 0 )
					continue;

				BlockPos neighbourPosition = new BlockPos( position.add( x, 0, z ) );
				BlockState blockState = world.getBlockState( neighbourPosition );

				if( blockState.getBlock() instanceof CropsBlock ) {
					CropsBlock cropsBlock = ( CropsBlock )blockState.getBlock();
					double growChance = Instances.HARVESTER.growChance.get();
					if( growChance > 0.0 ) {
						if( Random.tryChance( growChance ) ) {
							int penalty = Instances.HARVESTER.durabilityPenalty.get();
							cropsBlock.grow( world, neighbourPosition, blockState );
							spawnParticles( world, neighbourPosition, 3 );
							if( penalty > 0 )
								hoe.damageItem( penalty, entity, owner->owner.sendBreakAnimation( EquipmentSlotType.MAINHAND ) );
						}
						spawnParticles( world, neighbourPosition, 1 );
					}
				} else if( blockState.getBlock() instanceof NetherWartBlock ) {
					NetherWartBlock netherWartBlock = ( NetherWartBlock )blockState.getBlock();
					double growChance = Instances.HARVESTER.netherWartGrowChance.get();
					if( growChance > 0.0 ) {
						if( Random.tryChance( growChance ) ) {
							int penalty = Instances.HARVESTER.durabilityPenalty.get();
							int newAge = MathHelper.clamp( blockState.get( NetherWartBlock.AGE )+1, 0, 3 );
							world.setBlockState( neighbourPosition, netherWartBlock.getDefaultState().with( NetherWartBlock.AGE, newAge ) );
							spawnParticles( world, neighbourPosition, 3 );
							if( penalty > 0 )
								hoe.damageItem( penalty, entity, owner->owner.sendBreakAnimation( EquipmentSlotType.MAINHAND ) );
						}
						spawnParticles( world, neighbourPosition, 1 );
					}
				}
			}
	}

	/** Spawning particles on nearby crops. */
	protected static void spawnParticles( ServerWorld world, BlockPos position, int amount ) {
		world.spawnParticle( ParticleTypes.HAPPY_VILLAGER, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, amount, 0.25, 0.25,
			0.25, 0.1
		);
	}

	public static class Serializer extends GlobalLootModifierSerializer< Replant > {
		@Override
		public Replant read( ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn ) {
			return new Replant( conditionsIn );
		}

		@Override
		public JsonObject write( Replant instance ) {
			return null;
		}
	}
}

