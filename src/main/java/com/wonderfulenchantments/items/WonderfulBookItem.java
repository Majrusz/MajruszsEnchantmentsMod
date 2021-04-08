package com.wonderfulenchantments.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/** Class representing treasure bag. */
public class WonderfulBookItem extends Item {
	public WonderfulBookItem() {
		super( ( new Item.Properties() ).maxStackSize( 1 )
			.group( ItemGroup.MISC )
			.rarity( Rarity.UNCOMMON )
		);
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
	public void addInformation( ItemStack stack, @Nullable World world, List< ITextComponent > toolTip, ITooltipFlag flag ) {
		// MajruszsDifficulty.addExtraTooltipIfDisabled( toolTip, this.availability.isEnabled() );

		if( !flag.isAdvanced() )
			return;

		// toolTip.add( new TranslationTextComponent( "majruszs_difficulty.treasure_bag.item_tooltip" ).mergeStyle( TextFormatting.GRAY ) );
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
