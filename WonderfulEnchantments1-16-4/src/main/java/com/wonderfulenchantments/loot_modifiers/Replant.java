package com.wonderfulenchantments.loot_modifiers;

import com.google.gson.JsonObject;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.wonderfulenchantments.Instances;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Functionality of Harvest enchantment. */
public class Replant extends LootModifier {
	public Replant( ILootCondition[] conditionsIn ) {
		super( conditionsIn );
	}

	@Nonnull
	@Override
	public List< ItemStack > doApply( List< ItemStack > generatedLoot, LootContext context ) {
		BlockState blockState = context.get( LootParameters.BLOCK_STATE );
		if( blockState == null )
			return generatedLoot;

		if( !( blockState.getBlock() instanceof CropsBlock ) )
			return generatedLoot;

		CropsBlock crops = ( CropsBlock )blockState.getBlock();
		Entity entity = context.get( LootParameters.THIS_ENTITY );
		ItemStack hoe = context.get( LootParameters.TOOL );
		Vector3d origin = context.get( LootParameters.field_237457_g_ );
		if( entity == null || origin == null || !crops.isMaxAge( blockState ) )
			return generatedLoot;

		BlockPos position = new BlockPos( origin );
		removeSeedsFromLoot( generatedLoot, entity.world, crops, blockState, position );
		if( entity.world instanceof ServerWorld && entity instanceof LivingEntity )
			tickInRange( Instances.HARVEST.range.get(), ( ServerWorld )entity.world, position, ( LivingEntity )entity, hoe );

		return generatedLoot;
	}

	/** Removes one amount of seeds from loot if there are any. */
	protected static void removeSeedsFromLoot( List< ItemStack > generatedLoot, World world, CropsBlock crops, BlockState state, BlockPos position ) {
		ItemStack seedStack = crops.getItem( world, position, state );
		for( ItemStack itemStack : generatedLoot ) {
			if( itemStack.getItem() == seedStack.getItem() ) {
				itemStack.setCount( itemStack.getCount()-1 );
				world.setBlockState( position, crops.getDefaultState() );
				return;
			}
		}
	}

	/** Increases nearby crops age and damages hoe. */
	protected static void tickInRange( int range, ServerWorld world, BlockPos position, LivingEntity entity, ItemStack hoe ) {
		for( int z = -range; z <= range; z++ )
			for( int x = -range; x <= range; x++ ) {
				if( x == 0 && z == 0 )
					continue;

				BlockPos neighbourPosition = new BlockPos( position.add( x, 0, z ) );
				BlockState blockState = world.getBlockState( neighbourPosition );
				if( !( blockState.getBlock() instanceof CropsBlock ) )
					continue;

				CropsBlock cropsBlock = ( CropsBlock )blockState.getBlock();
				double growChance = Instances.HARVEST.growChance.get();
				if( growChance > 0.0 ) {
					if( Random.tryChance( growChance ) ) {
						cropsBlock.grow( world, neighbourPosition, blockState );
						hoe.damageItem( 1, entity, owner->owner.sendBreakAnimation( EquipmentSlotType.MAINHAND ) );
					}
					world.spawnParticle( ParticleTypes.HAPPY_VILLAGER, neighbourPosition.getX() + 0.5, neighbourPosition.getY() + 0.5, neighbourPosition.getZ() + 0.5, 4, 0.25, 0.25, 0.25, 0.1 );
				}
			}
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

