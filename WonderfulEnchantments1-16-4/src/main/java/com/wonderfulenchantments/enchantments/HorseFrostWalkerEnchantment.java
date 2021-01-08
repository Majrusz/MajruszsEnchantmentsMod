package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that gives Frost Walker enchantment effect but on horse armor. */
@Mod.EventBusSubscriber
public class HorseFrostWalkerEnchantment extends Enchantment {
	public HorseFrostWalkerEnchantment() {
		super( Rarity.RARE, WonderfulEnchantmentHelper.HORSE_ARMOR, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return level * 10 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 15;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	/** Event that freezes nearby water each tick when all conditions are met. */
	@SubscribeEvent
	public static void freezeNearby( LivingEvent.LivingUpdateEvent event ) {
		if( !isValid( event ) )
			return;

		AnimalEntity animal = ( AnimalEntity )event.getEntityLiving();
		ServerWorld world = ( ServerWorld )animal.world;
		BlockPos position = new BlockPos( animal.getPositionVec() );
		int enchantmentLevel = WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.HORSE_FROST_WALKER.get(),
			animal.getArmorInventoryList()
		);
		BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
		double factor = Math.min( 16, 2 + enchantmentLevel );
		BlockPos.Mutable mutablePosition = new BlockPos.Mutable();
		Iterable< BlockPos > blockPositions = BlockPos.getAllInBoxMutable( position.add( -factor, -1.0D, -factor ),
			position.add( factor, -1.0D, factor )
		);

		for( BlockPos blockPosition : blockPositions ) {
			if( !blockPosition.withinDistance( animal.getPositionVec(), factor ) )
				continue;

			mutablePosition.setPos( blockPosition.getX(), blockPosition.getY() + 1.0, blockPosition.getZ() );
			BlockState blockAboveState = world.getBlockState( mutablePosition );

			if( !blockAboveState.isAir( world, mutablePosition ) )
				continue;

			BlockState currentBlockState = world.getBlockState( blockPosition );

			boolean isFull = currentBlockState.getBlock() == Blocks.WATER && currentBlockState.get( FlowingFluidBlock.LEVEL ) == 0;
			if( !isFull )
				continue;

			boolean isWater = currentBlockState.getMaterial() == Material.WATER;
			if( !isWater )
				continue;

			boolean isValid = blockState.isValidPosition( world, blockPosition ) && world.placedBlockCollides( blockState, blockPosition,
				ISelectionContext.dummy()
			);
			if( !isValid )
				continue;

			boolean hasPlacingSucceeded = !net.minecraftforge.event.ForgeEventFactory.onBlockPlace( animal,
				net.minecraftforge.common.util.BlockSnapshot.create( world.getDimensionKey(), world, blockPosition ), net.minecraft.util.Direction.UP
			);
			if( !hasPlacingSucceeded )
				continue;

			world.setBlockState( blockPosition, blockState );
			world.getPendingBlockTicks()
				.scheduleTick( blockPosition, Blocks.FROSTED_ICE, MathHelper.nextInt( animal.getRNG(), 60, 120 ) );
		}
	}

	/** Checking whether all conditions are met.
	 @param event Living entity update event.
	 */
	protected static boolean isValid( LivingEvent.LivingUpdateEvent event ) {
		if( !( event.getEntityLiving() instanceof AnimalEntity ) )
			return false;

		AnimalEntity animal = ( AnimalEntity )event.getEntityLiving();

		if( !animal.isServerWorld() )
			return false;

		if( !animal.isOnGround() )
			return false;

		for( ItemStack itemStack : animal.getArmorInventoryList() )
			if( WonderfulEnchantmentHelper.isHorseArmor( itemStack ) )
				return true;

		return false;
	}
}
