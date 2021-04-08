package com.wonderfulenchantments.items;

import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static com.wonderfulenchantments.WonderfulEnchantments.ITEM_GROUP;

/** Class representing treasure bag. */
public class WonderfulBookItem extends Item {
	private static final String BOOK_TAG = "wonderful_book";
	private static final String ENERGY_TAG = "energy";
	protected final ConfigGroup itemGroup;
	protected final IntegerConfig startingLevel;
	protected final IntegerConfig maximumLevel;
	protected final IntegerConfig minimumCost;
	protected final DoubleConfig costRatio;

	public WonderfulBookItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( ItemGroup.MISC )
			.rarity( Rarity.UNCOMMON )
		);

		String startComment = "Starting enchanting power level.";
		String maximumComment = "Maximum level of enchanting power.";
		String minimumComment = "Minimum cost of enchanting.";
		String ratioComment = "Enchanting cost ratio for each extra level beyond starting enchanting power level.";
		this.startingLevel = new IntegerConfig( "starting_level", startComment, false, 6, 6, 100 );
		this.maximumLevel = new IntegerConfig( "maximum_level", maximumComment, false, 30, 6, 100 );
		this.minimumCost = new IntegerConfig( "minimum_cost", minimumComment, false, 6, 6, 100 );
		this.costRatio = new DoubleConfig( "cost_ratio", ratioComment, false, 0.5, 0.0, 1.0 );
		this.itemGroup = ITEM_GROUP.addGroup( new ConfigGroup( "WonderfulBook", "" ) );
		this.itemGroup.addConfigs( this.startingLevel, this.maximumLevel, this.minimumCost, this.costRatio );
	}

	/** Enchanting ultimate book on right click. */
	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack ultimateBook = player.getHeldItem( hand );

		if( !world.isRemote ) {
			ItemStack enchantedBook = new ItemStack( Items.ENCHANTED_BOOK );
			player.setHeldItem( hand, enchantedBook );

			player.addStat( Stats.ITEM_USED.get( this ) );
			world.playSound( null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.AMBIENT, 1.0f, 0.9f );

			return ActionResult.func_233538_a_( ultimateBook, world.isRemote() );
		}

		return ActionResult.func_233538_a_( ultimateBook, world.isRemote() );
	}

	/** Adding simple tooltip to treasure bag. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		toolTip.add( new StringTextComponent( "Enchanting power: " ).appendString( "" + getEnergyLevel( itemStack ) + " level" ) );
		toolTip.add( new StringTextComponent( "Enchanting cost: " ).appendString( "" + getEnchantingLevelCost( itemStack ) + " levels" ) );

		if( !flag.isAdvanced() )
			return;

		// toolTip.add( new TranslationTextComponent( "majruszs_difficulty.treasure_bag.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
	}

	/** Returns current book energy level. */
	public int getEnergyLevel( ItemStack itemStack ) {
		CompoundNBT compoundNBT = itemStack.getChildTag( BOOK_TAG );
		return compoundNBT != null && compoundNBT.contains( ENERGY_TAG, 99 ) ? compoundNBT.getInt( ENERGY_TAG ) : this.startingLevel.get();
	}

	/** Sets new energy level for given item stack. */
	public void setEnergyLevel( ItemStack itemStack, int energyLevel ) {
		CompoundNBT compoundNBT = itemStack.getOrCreateChildTag( BOOK_TAG );
		compoundNBT.putInt( ENERGY_TAG, Math.max( this.startingLevel.get(), Math.min( energyLevel, this.maximumLevel.get() ) ) );
	}

	/** Increases energy level depending on given items. */
	public ItemStack energizeBook( ItemStack itemStack, List< ItemStack > energizingItemsList ) {
		setEnergyLevel( itemStack, getEnergyLevel( itemStack ) + energizingItemsList.size() );

		return itemStack;
	}

	/** Returns enchanting level cost depending on energy level. */
	public int getEnchantingLevelCost( ItemStack itemStack ) {
		return this.minimumCost.get() + ( int )Math.max( 0, ( getEnergyLevel( itemStack ) - this.minimumCost.get() )*this.costRatio.get() );
	}



	/** Checks whether treasure bag is not disabled in configuration file? */
	/*public boolean isAvailable() {
		return this.availability.isEnabled();
	}*/

	/** Generating loot context of current treasure bag. (who opened the bag, where, etc.) */
	/*protected static LootContext generateLootContext( PlayerEntity player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerWorld )player.getEntityWorld() );
		lootContextBuilder.withParameter( LootParameters.field_237457_g_, player.getPositionVec() );
		lootContextBuilder.withParameter( LootParameters.THIS_ENTITY, player );

		return lootContextBuilder.build( LootParameterSets.GIFT );
	}*/
}
