package com.wonderfulenchantments.enchantments;

import com.mlib.MajruszLibrary;
import com.mlib.TimeConverter;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.nbt.NBTHelper;
import com.mlib.network.FloatMessage;
import com.mlib.time.TimeHelper;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases mining speed the longer the player hold left mouse button. */
@Mod.EventBusSubscriber
public class GottaMineFastEnchantment extends WonderfulEnchantment {
	private static final String COUNTER_TAG = "GottaMineFastCounter";
	private static final String MINING_MULTIPLIER_TAG = "GottaMineFastMultiplier";
	protected final DoubleConfig exponent;
	protected final DurationConfig maximumDuration;
	protected boolean isMining = false;

	public GottaMineFastEnchantment() {
		super( "gotta_mine_fast", Rarity.RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, "GottaMineFast" );

		String exponentComment = "Duration is raised to this exponent. (for example exponent = 1.5 and after two minutes bonus is equal 2.0 ^ 1.5 = 2.82 (total 3.82))";
		this.exponent = new DoubleConfig( "exponent", exponentComment, false, 1.5849625007, 1.01, 5.0 );

		String durationComment = "Maximum duration increasing mining speed. (in seconds)";
		this.maximumDuration = new DurationConfig( "maximum_duration", durationComment, false, 120.0, 1.0, 3600.0 );

		this.enchantmentGroup.addConfigs( this.exponent, this.maximumDuration );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 25 );
		setMinimumEnchantabilityCalculator( level->( 15 * level ) );
	}

	/** Event that sets flag when the player is holding left mouse button. */
	@SubscribeEvent
	public static void whenHoldingMouseButton( InputEvent.MouseInputEvent event ) {
		if( event.getButton() == 0 )
			Instances.GOTTA_MINE_FAST.isMining = event.getAction() == 1;
	}

	/** Event that increases ticks when player is holding left mouse button. */
	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		Player player = event.player;
		if( player.level instanceof ServerLevel || !TimeHelper.isEndPhase( event ) )
			return;

		GottaMineFastEnchantment gottaMineFast = Instances.GOTTA_MINE_FAST;
		NBTHelper.IntegerData counterData = new NBTHelper.IntegerData( player, COUNTER_TAG );
		counterData.set( value->gottaMineFast.isMining ? value + 1 : 0 );

		if( TimeHelper.hasClientTicksPassed( 20 ) )
			gottaMineFast.sendMultiplierMessage( counterData );
	}

	/** Event that increases damage dealt to block each tick when player is holding left mouse button and have this enchantment. */
	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		Player player = event.getPlayer();
		GottaMineFastEnchantment gottaMineFast = Instances.GOTTA_MINE_FAST;
		int enchantmentLevel = gottaMineFast.getEnchantmentLevel( player );

		if( enchantmentLevel > 0 ) {
			NBTHelper.FloatData miningData = new NBTHelper.FloatData( player, MINING_MULTIPLIER_TAG );
			NBTHelper.IntegerData counterData = new NBTHelper.IntegerData( player, COUNTER_TAG );

			float miningMultiplier = gottaMineFast.getMiningMultiplier( counterData.get() );
			if( miningMultiplier > 0.0f )
				event.setNewSpeed( event.getNewSpeed() * ( 1.0f + miningMultiplier ) );
			else
				event.setNewSpeed( event.getNewSpeed() * ( 1.0f + miningData.get() ) );
		}
	}

	public void sendMultiplierMessage( NBTHelper.IntegerData counterData ) {
		PacketHandler.CHANNEL.sendToServer( new MultiplierMessage( getMiningMultiplier( counterData.get() ) ) );
	}

	/**
	 Calculating mining multiplier depending on ticks the player was holding left mouse.

	 @return Returns multiplier which represents how fast the player will mine the block. (2.0f will mean twice as fast)
	 */
	protected float getMiningMultiplier( int miningTicks ) {
		return ( float )Math.pow(
			Math.min( miningTicks, this.maximumDuration.getDuration() ) / ( float )TimeConverter.minutesToTicks( 1.0 ), this.exponent.get()
		);
	}

	/** Sends information from client to server about how long player holded mouse button. */
	public static class MultiplierMessage extends FloatMessage {
		public MultiplierMessage( float value ) {
			super( value );
		}

		public MultiplierMessage( FriendlyByteBuf buffer ) {
			super( buffer );
		}

		@Override
		public void receiveMessage( ServerPlayer sender, CompoundTag data ) {
			data.putFloat( MINING_MULTIPLIER_TAG, this.value );
		}
	}
}
