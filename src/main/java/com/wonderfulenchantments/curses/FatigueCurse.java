package com.wonderfulenchantments.curses;

import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Reduces player mining speed. */
@Mod.EventBusSubscriber
public class FatigueCurse extends WonderfulCurse {
	protected final DoubleConfig miningMultiplierConfig;

	public FatigueCurse() {
		super( "fatigue_curse", Rarity.RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, "Fatigue" );

		String comment = "Mining speed reduction with each level.";
		this.miningMultiplierConfig = new DoubleConfig( "multiplier", comment, false, 0.8, 0.1, 0.95 );

		this.curseGroup.addConfig( this.miningMultiplierConfig );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DiggingEnchantment ) && super.checkCompatibility( enchantment );
	}

	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		FatigueCurse fatigueCurse = Instances.FATIGUE;
		int fatigueLevel = fatigueCurse.getEnchantmentLevel( event.getPlayer() );

		if( fatigueLevel > 0 )
			event.setNewSpeed( event.getNewSpeed() * fatigueCurse.getMiningMultiplier( fatigueLevel ) );
	}

	/** Returns final mining multiplier. */
	protected float getMiningMultiplier( int fatigueLevel ) {
		return ( float )Math.pow( this.miningMultiplierConfig.get(), fatigueLevel );
	}
}
