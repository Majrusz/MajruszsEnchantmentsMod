package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that automatically replants seeds and gives a chance to improve nearby crops. */
@Mod.EventBusSubscriber
public class HarvesterEnchantment extends WonderfulEnchantment {
	public final IntegerConfig range;
	public final IntegerConfig durabilityPenalty;
	public final DoubleConfig growChance;
	public final DoubleConfig netherWartGrowChance;

	public HarvesterEnchantment() {
		super( "harvester", Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, "Harvester" );
		String rangeComment = "Range increase per enchantment level. (per block in x-axis and z-axis)";
		String durabilityComment = "Penalty for increasing age of nearby crops. (per successful increase)";
		String growComment = "Chance for increasing age of nearby crops. (calculated for each crop separately)";
		String wartComment = "Chance for increasing age of nearby Nether Warts. (calculated for each crop separately)";
		this.range = new IntegerConfig( "range", rangeComment, false, 1, 1, 3 );
		this.durabilityPenalty = new IntegerConfig( "durability_penalty", durabilityComment, false, 1, 1, 10 );
		this.growChance = new DoubleConfig( "grow_chance", growComment, false, 0.04, 0.0, 1.0 );
		this.netherWartGrowChance = new DoubleConfig( "nether_wart_grow_chance", wartComment, false, 0.01, 0.0, 1.0 );
		this.enchantmentGroup.addConfigs( this.range, this.durabilityPenalty, this.growChance, this.netherWartGrowChance );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 10 * level ) );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack ) {
		return stack.getItem() instanceof HoeItem && super.canApplyAtEnchantingTable( stack );
	}

	/** Adding possibility to harvest crops with right click. */
	@SubscribeEvent
	public static void onRightClick( PlayerInteractEvent.RightClickBlock event ) {
		ItemStack itemStack = event.getItemStack();
		int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel( Instances.HARVESTER, itemStack );
		Player player = event.getPlayer();
		BlockPos position = event.getPos();
		if( enchantmentLevel <= 0 )
			return;

		BlockState blockState = player.level.getBlockState( position );
		Block block = blockState.getBlock();
		if( block instanceof CropBlock )
			handleCrops( ( CropBlock )block, player, position, blockState, itemStack );
		else if( block instanceof NetherWartBlock )
			handleNetherWarts( ( NetherWartBlock )block, player, position, blockState, itemStack );
	}

	/** Harvests any crops block. */
	protected static void handleCrops( CropBlock crops, Player player, BlockPos position, BlockState blockState, ItemStack itemStack ) {
		if( !crops.isMaxAge( blockState ) )
			return;

		crops.playerDestroy( player.level, player, position, blockState, null, itemStack );
		playSound( player, position );
	}

	/** Harvests nether wart block. */
	protected static void handleNetherWarts( NetherWartBlock netherWartBlock, Player player, BlockPos position, BlockState blockState, ItemStack itemStack ) {
		int netherWartMaximumAge = 3;
		if( blockState.getValue( NetherWartBlock.AGE ) < netherWartMaximumAge )
			return;

		netherWartBlock.playerDestroy( player.level, player, position, blockState, null, itemStack );
		playSound( player, position );
	}

	/** Plays harvest sound at given position. */
	protected static void playSound( Player player, BlockPos position ) {
		player.level.playSound( null, position, SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 0.25f, 0.5f );
	}
}
