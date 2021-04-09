package com.wonderfulenchantments.items;

import com.mlib.MajruszLibrary;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.wonderfulenchantments.WonderfulEnchantments.ITEM_GROUP;

/** Wonderful Book that can be converted to Enchanting Book with enchantments depending on enchanting power. */
public class WonderfulBookItem extends Item {
	private static final String BOOK_TAG = "wonderful_book";
	private static final String ENERGY_TAG = "energy";
	protected final ConfigGroup itemGroup;
	protected final IntegerConfig startingLevel;
	protected final IntegerConfig maximumLevel;
	protected final IntegerConfig minimumCost;
	protected final IntegerConfig amountOfBooks;
	protected final DoubleConfig costRatio;

	public WonderfulBookItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( ItemGroup.MISC )
			.rarity( Rarity.UNCOMMON )
		);

		String startComment = "Starting enchanting power level.";
		String maximumComment = "Maximum level of enchanting power.";
		String minimumComment = "Minimum cost of enchanting.";
		String bookComment = "Amount of books combined together.";
		String ratioComment = "Enchanting cost ratio for each extra level beyond starting enchanting power level.";
		this.startingLevel = new IntegerConfig( "starting_level", startComment, false, 6, 6, 100 );
		this.maximumLevel = new IntegerConfig( "maximum_level", maximumComment, false, 30, 6, 100 );
		this.minimumCost = new IntegerConfig( "minimum_cost", minimumComment, false, 6, 6, 100 );
		this.amountOfBooks = new IntegerConfig( "book_amount", bookComment, false, 2, 1, 10 );
		this.costRatio = new DoubleConfig( "cost_ratio", ratioComment, false, 0.75, 0.0, 1.0 );
		this.itemGroup = ITEM_GROUP.addGroup( new ConfigGroup( "WonderfulBook", "" ) );
		this.itemGroup.addConfigs( this.startingLevel, this.maximumLevel, this.minimumCost, this.amountOfBooks, this.costRatio );
	}

	/** Enchanting Wonderful Book on right click. */
	@Override
	public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity player, Hand hand ) {
		ItemStack wonderfulBook = player.getHeldItem( hand );

		if( !world.isRemote ) {
			ItemStack enchantedBook = new ItemStack( Items.ENCHANTED_BOOK );

			player.addStat( Stats.ITEM_USED.get( this ) );
			world.playSound( null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.AMBIENT, 1.0f, 0.9f );

			List< EnchantmentData > enchantmentDataList = new ArrayList<>();
			for( int i = 0; i < this.amountOfBooks.get(); ++i )
				enchantmentDataList.addAll( EnchantmentHelper.buildEnchantmentList( MajruszLibrary.RANDOM, new ItemStack( Items.BOOK ), getEnergyLevel( wonderfulBook ), false ) );

			removeIncompatibleEnchantments( enchantmentDataList );

			for( EnchantmentData enchantmentData : enchantmentDataList )
				EnchantedBookItem.addEnchantment( enchantedBook, enchantmentData );

			player.setHeldItem( hand, enchantedBook );
			return ActionResult.func_233538_a_( enchantedBook, world.isRemote() );
		}

		return ActionResult.func_233538_a_( wonderfulBook, world.isRemote() );
	}

	/** Adding simple tooltip to Wonderful Book about enchanting power, cost etc. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void addInformation( ItemStack itemStack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		IFormattableTextComponent powerText;
		if( hasMaximumPowerLevel( itemStack ) ) {
			powerText = new TranslationTextComponent( "item.wonderful_enchantments.wonderful_book.enchanting_power_max" );
			powerText.mergeStyle( TextFormatting.DARK_GREEN );
		} else {
			powerText = new TranslationTextComponent( "item.wonderful_enchantments.wonderful_book.enchanting_power" );
			powerText.appendString( " " + getEnergyLevel( itemStack ) + " " );
			powerText.append( new TranslationTextComponent( "item.wonderful_enchantments.wonderful_book.level" ) );
			powerText.mergeStyle( TextFormatting.GRAY );
		}
		toolTip.add( powerText );

		IFormattableTextComponent costText = new TranslationTextComponent( "item.wonderful_enchantments.wonderful_book.enchanting_cost" );
		costText.appendString( " " + getEnchantingLevelCost( itemStack ) + " " );
		costText.append( new TranslationTextComponent( "item.wonderful_enchantments.wonderful_book.levels" ) );
		costText.mergeStyle( TextFormatting.GRAY );
		toolTip.add( costText );

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

	/** Checks whether book has maximum enchanting power level. */
	public boolean hasMaximumPowerLevel( ItemStack itemStack ) {
		return getEnergyLevel( itemStack ) >= this.maximumLevel.get();
	}

	/** Remove all incompatible enchantments. */
	private void removeIncompatibleEnchantments( List< EnchantmentData > enchantmentDataList ) {
		int size = enchantmentDataList.size();

		for( int i = 0; i < size-1; ++i ) {
			EnchantmentData current = enchantmentDataList.get( i );
			for( int j = i+1; j < size; ) {
				EnchantmentData next = enchantmentDataList.get( j );

				if( !current.enchantment.isCompatibleWith( next.enchantment ) ) {
					enchantmentDataList.remove( j );
					--size;
				} else {
					++j;
				}
			}
		}
	}
}
