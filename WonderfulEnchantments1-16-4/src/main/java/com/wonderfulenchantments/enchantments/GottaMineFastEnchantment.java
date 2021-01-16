package com.wonderfulenchantments.enchantments;

import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases mining speed the longer the player hold left mouse button. */
@Mod.EventBusSubscriber
public class GottaMineFastEnchantment extends WonderfulEnchantment {
	private static final String COUNTER_TAG = "GottaMineFastCounter";
	private static boolean IS_MINING = false;
	protected final DoubleConfig exponent;
	protected final DurationConfig maximumDuration;

	public GottaMineFastEnchantment() {
		super( Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "GottaMineFast" );
		String exponent_comment = "Duration is raised to this exponent. (for example exponent = 1.5 and after two seconds bonus is equal 2.0 ^ 1.5 = 2.82 (total 3.82))";
		String duration_comment = "Maximum duration increasing mining speed. (in seconds)";
		this.exponent = new DoubleConfig( "exponent", exponent_comment, false, 1.5849625007, 1.01, 5.0 );
		this.maximumDuration = new DurationConfig( "maximum_duration", duration_comment, false, 120.0, 1.0, 3600.0 );
		this.enchantmentGroup.addConfigs( this.exponent, this.maximumDuration );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 25 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}

	/** Event that sets flag when the player is holding left mouse button. */
	@SubscribeEvent
	public static void whenHoldingMouseButton( InputEvent.MouseInputEvent event ) {
		if( event.getButton() == 0 )
			IS_MINING = event.getAction() == 1;
	}

	/** Event that increases ticks when player is holding left mouse button. */
	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;
		CompoundNBT data = player.getPersistentData();

		data.putInt( COUNTER_TAG, IS_MINING ? data.getInt( COUNTER_TAG ) + 1 : 0 );
	}

	/** Event that increases damage dealt to block each tick when player is holding left mouse button and have this enchantment. */
	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		PlayerEntity player = event.getPlayer();
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( Instances.GOTTA_MINE_FAST, player );

		if( enchantmentLevel > 0 )
			event.setNewSpeed( event.getNewSpeed() * getMiningMultiplier( player ) );

		MajruszLibrary.LOGGER.debug( getMiningMultiplier( player ) );
	}

	/**
	 Calculating mining multiplier depending on ticks the player was holding left mouse.

	 @param player Player to calculate bonus on.

	 @return Returns multiplier which represents how fast the player will mine the block. (2.0f will mean twice as fast)
	 */
	protected static float getMiningMultiplier( PlayerEntity player ) {
		GottaMineFastEnchantment enchantment = Instances.GOTTA_MINE_FAST;
		CompoundNBT data = player.getPersistentData();

		return 1.0f + ( float )Math.pow(
			Math.min( data.getInt( COUNTER_TAG ), enchantment.maximumDuration.getDuration() ) / ( float )TimeConverter.minutesToTicks( 1.0 ),
			enchantment.exponent.get()
		);
	}
}
