package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.MajruszLibrary;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that gives Frost Walker enchantment effect but on horse armor. */
@Mod.EventBusSubscriber
public class HorseFrostWalkerEnchantment extends WonderfulEnchantment {
	public HorseFrostWalkerEnchantment() {
		super( "horse_frost_walker", Rarity.RARE, RegistryHandler.HORSE_ARMOR, EquipmentSlotTypes.ARMOR, "HorseFrostWalker" );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 15 );
		setMinimumEnchantabilityCalculator( level->( 10 * level ) );
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
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.HORSE_FROST_WALKER, animal.getArmorInventoryList() );
		BlockState blockState = Blocks.FROSTED_ICE.getDefaultState();
		double factor = Math.min( 16, 2 + enchantmentLevel );
		BlockPos.Mutable mutablePosition = new BlockPos.Mutable();
		Iterable< BlockPos > blockPositions = BlockPos.getAllInBoxMutable( position.add( -factor, -1.0D, -factor ),
			position.add( factor, -1.0D, factor )
		);

		if( enchantmentLevel <= 0 )
			return;

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

	/** Disabling taking damage when horse is standing on Magma Block. */
	@SubscribeEvent
	public static void onTakingDamage( LivingDamageEvent event ) {
		if( !( event.getEntityLiving() instanceof AnimalEntity ) || !( event.getSource() == DamageSource.HOT_FLOOR ) )
			return;

		AnimalEntity animal = ( AnimalEntity )event.getEntityLiving();
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.HORSE_FROST_WALKER, animal.getArmorInventoryList() );
		if( enchantmentLevel > 0 )
			event.setCanceled( true );
	}

	/**
	 Checking whether all conditions are met.

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
			if( itemStack.getItem() instanceof HorseArmorItem )
				return true;

		return false;
	}
}
