package com.wonderfulenchantments.curses;

import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Reduces player mining speed. */
@Mod.EventBusSubscriber
public class FatigueCurse extends WonderfulCurse {
	protected final DoubleConfig miningMultiplierConfig;

	public FatigueCurse() {
		super( Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "Fatigue" );
		String comment = "Mining speed reduction with each level.";
		this.miningMultiplierConfig = new DoubleConfig( "multiplier", comment, false, 0.8, 0.1, 0.95 );
		this.curseGroup.addConfig( this.miningMultiplierConfig );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof EfficiencyEnchantment ) && super.canApplyTogether( enchantment );
	}

	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		FatigueCurse fatigueCurse = Instances.FATIGUE;
		int fatigueLevel = EnchantmentHelper.getMaxEnchantmentLevel( fatigueCurse, event.getPlayer() );

		if( fatigueLevel > 0 )
			event.setNewSpeed( event.getNewSpeed() * fatigueCurse.getMiningMultiplier( fatigueLevel ) );
	}

	/** Returns final mining multiplier. */
	protected float getMiningMultiplier( int fatigueLevel ) {
		return ( float )Math.pow( this.miningMultiplierConfig.get(), fatigueLevel );
	}
}
