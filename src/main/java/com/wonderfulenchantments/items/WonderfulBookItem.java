package com.wonderfulenchantments.items;

import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.entities.EntityHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.wonderfulenchantments.WonderfulEnchantments.ITEM_GROUP;

/** Wonderful Book that can be converted to Enchanting Book with enchantments depending on enchanting energy. */
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
		super( ( new Item.Properties() ).stacksTo( 1 )
			.tab( CreativeModeTab.TAB_MISC )
			.rarity( Rarity.UNCOMMON ) );

		String startComment = "Starting enchanting energy level.";
		this.startingLevel = new IntegerConfig( "starting_level", startComment, false, 6, 6, 100 );

		String maximumComment = "Maximum level of enchanting energy.";
		this.maximumLevel = new IntegerConfig( "maximum_level", maximumComment, false, 30, 6, 100 );

		String minimumComment = "Minimum cost of enchanting.";
		this.minimumCost = new IntegerConfig( "minimum_cost", minimumComment, false, 6, 6, 100 );

		String bookComment = "Amount of books combined together.";
		this.amountOfBooks = new IntegerConfig( "book_amount", bookComment, false, 3, 1, 10 );

		String ratioComment = "Enchanting cost ratio for each extra level beyond starting enchanting energy level.";
		this.costRatio = new DoubleConfig( "cost_ratio", ratioComment, false, 0.75, 0.0, 1.0 );

		this.itemGroup = ITEM_GROUP.addGroup( new ConfigGroup( "WonderfulBook", "" ) );
		this.itemGroup.addConfigs( this.startingLevel, this.maximumLevel, this.minimumCost, this.amountOfBooks, this.costRatio );
	}

	/** Enchanting Wonderful Book on right click. */
	@Override
	public InteractionResultHolder< ItemStack > use( Level world, Player player, InteractionHand hand ) {
		ItemStack wonderfulBook = player.getItemInHand( hand );

		if( !world.isClientSide ) {
			int levelCost = getEnchantingLevelCost( wonderfulBook );
			boolean isOnCreativeMode = EntityHelper.isOnCreativeMode( player );
			if( player.experienceLevel >= levelCost ) {
				if( !isOnCreativeMode )
					player.giveExperienceLevels( -levelCost );
			} else if( !isOnCreativeMode ) {
				return InteractionResultHolder.sidedSuccess( wonderfulBook, world.isClientSide() );
			}
			ItemStack enchantedBook = new ItemStack( Items.ENCHANTED_BOOK );

			player.awardStat( Stats.ITEM_USED.get( this ) );
			world.playSound( null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT, 0.75f, 0.75f );

			List< EnchantmentInstance > enchantmentDataList = new ArrayList<>();
			for( int i = 0; i < this.amountOfBooks.get(); ++i )
				enchantmentDataList.addAll(
					EnchantmentHelper.selectEnchantment( MajruszLibrary.RANDOM, new ItemStack( Items.BOOK ), getEnergyLevel( wonderfulBook ),
						false
					) );

			removeIncompatibleEnchantments( enchantmentDataList );

			for( EnchantmentInstance enchantmentData : enchantmentDataList )
				EnchantedBookItem.addEnchantment( enchantedBook, enchantmentData );

			player.setItemInHand( hand, enchantedBook );
			return InteractionResultHolder.sidedSuccess( enchantedBook, world.isClientSide() );
		}

		return InteractionResultHolder.sidedSuccess( wonderfulBook, world.isClientSide() );
	}

	/** Adding simple tooltip to Wonderful Book about enchanting energy, cost etc. */
	@Override
	@OnlyIn( Dist.CLIENT )
	public void appendHoverText( ItemStack itemStack, @Nullable Level world, List< Component > toolTip, TooltipFlag flag ) {
		MutableComponent energyText;
		if( hasMaximumEnergyLevel( itemStack ) && world != null && ( world.getGameTime() / TimeConverter.secondsToTicks( 2.0 ) ) % 3 == 0 ) {
			energyText = new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.enchanting_energy_max" );
			energyText.withStyle( ChatFormatting.BLUE );
		} else {
			energyText = new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.enchanting_energy" );
			energyText.append( " " + getEnergyLevel( itemStack ) + " " );
			energyText.append( new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.level" ) );
			energyText.withStyle( ChatFormatting.GRAY );
		}
		toolTip.add( energyText );

		MutableComponent costText = new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.enchanting_cost" );
		costText.append( " " + getEnchantingLevelCost( itemStack ) + " " );
		costText.append( new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.levels" ) );
		costText.withStyle( ChatFormatting.GRAY );
		toolTip.add( costText );

		if( getEnergyLevel( itemStack ) < this.startingLevel.get() + 1 ) {
			toolTip.add( new TextComponent( " " ) );
			toolTip.add( new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.hint" ).withStyle( ChatFormatting.GRAY ) );
		}

		toolTip.add( new TextComponent( " " ) );
		toolTip.add( new TranslatableComponent( "item.wonderful_enchantments.wonderful_book.transmute" ).withStyle( ChatFormatting.GRAY ) );
	}

	@Override
	public void fillItemCategory( CreativeModeTab itemGroup, NonNullList< ItemStack > itemStacks ) {
		double max = this.maximumLevel.get(), min = this.startingLevel.get();
		double range = max - min;
		for( int i = 0; i <= 2; ++i ) {
			ItemStack wonderfulBook = new ItemStack( this );
			setEnergyLevel( wonderfulBook, ( int )( min + range * i / 2.0f ) );
			itemStacks.add( wonderfulBook );
		}
	}

	/** Returns current book energy level. */
	public int getEnergyLevel( ItemStack itemStack ) {
		CompoundTag compoundNBT = itemStack.getTagElement( BOOK_TAG );
		return compoundNBT != null && compoundNBT.contains( ENERGY_TAG, 99 ) ? compoundNBT.getInt( ENERGY_TAG ) : this.startingLevel.get();
	}

	/** Sets new energy level for given item stack. */
	public void setEnergyLevel( ItemStack itemStack, int energyLevel ) {
		CompoundTag compoundNBT = itemStack.getOrCreateTagElement( BOOK_TAG );
		compoundNBT.putInt( ENERGY_TAG, Math.max( this.startingLevel.get(), Math.min( energyLevel, this.maximumLevel.get() ) ) );
	}

	/** Increases energy level depending on given items. */
	public ItemStack energizeBook( ItemStack itemStack, List< ItemStack > energizingItemsList ) {
		setEnergyLevel( itemStack, getEnergyLevel( itemStack ) + energizingItemsList.size() );

		return itemStack;
	}

	/** Returns enchanting level cost depending on energy level. */
	public int getEnchantingLevelCost( ItemStack itemStack ) {
		return this.minimumCost.get() + ( int )Math.max( 0, ( getEnergyLevel( itemStack ) - this.minimumCost.get() ) * this.costRatio.get() );
	}

	/** Checks whether book has maximum enchanting energy level. */
	public boolean hasMaximumEnergyLevel( ItemStack itemStack ) {
		return getEnergyLevel( itemStack ) >= this.maximumLevel.get();
	}

	/** Remove all incompatible enchantments. */
	private void removeIncompatibleEnchantments( List< EnchantmentInstance > enchantmentDataList ) {
		int size = enchantmentDataList.size();

		for( int i = 0; i < size - 1; ++i ) {
			EnchantmentInstance current = enchantmentDataList.get( i );
			for( int j = i + 1; j < size; ) {
				EnchantmentInstance next = enchantmentDataList.get( j );

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
