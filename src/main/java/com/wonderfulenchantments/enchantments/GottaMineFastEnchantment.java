package com.wonderfulenchantments.enchantments;

import com.mlib.TimeConverter;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.PacketHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/** Enchantment that increases mining speed the longer the player hold left mouse button. */
@Mod.EventBusSubscriber
public class GottaMineFastEnchantment extends WonderfulEnchantment {
	private static final String COUNTER_TAG = "GottaMineFastCounter";
	private static final String MINING_MULTIPLIER_TAG = "GottaMineFastMultiplier";
	protected final DoubleConfig exponent;
	protected final DurationConfig maximumDuration;
	protected int counter = 0;
	protected boolean isMining = false;

	public GottaMineFastEnchantment() {
		super( "gotta_mine_fast", Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND, "GottaMineFast" );
		String exponent_comment = "Duration is raised to this exponent. (for example exponent = 1.5 and after two minutes bonus is equal 2.0 ^ 1.5 = 2.82 (total 3.82))";
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
			Instances.GOTTA_MINE_FAST.isMining = event.getAction() == 1;
	}

	/** Event that increases ticks when player is holding left mouse button. */
	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		PlayerEntity player = event.player;
		if( player.world instanceof ServerWorld )
			return;

		CompoundNBT data = player.getPersistentData();
		data.putInt( COUNTER_TAG, Instances.GOTTA_MINE_FAST.isMining ? data.getInt( COUNTER_TAG ) + 1 : 0 );
		Instances.GOTTA_MINE_FAST.counter = ( Instances.GOTTA_MINE_FAST.counter + 1 ) % 20;

		if( Instances.GOTTA_MINE_FAST.counter == 0 )
			PacketHandler.CHANNEL.sendToServer( new GottaMineFastMultiplier( getMiningMultiplier( player ) ) );
	}

	/** Event that increases damage dealt to block each tick when player is holding left mouse button and have this enchantment. */
	@SubscribeEvent
	public static void onBreakingBlock( PlayerEvent.BreakSpeed event ) {
		PlayerEntity player = event.getPlayer();
		CompoundNBT data = player.getPersistentData();
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( Instances.GOTTA_MINE_FAST, player );

		if( enchantmentLevel > 0 ) {
			if( getMiningMultiplier( player ) > 0.0f )
				event.setNewSpeed( event.getNewSpeed() * ( 1.0f + getMiningMultiplier( player ) ) );
			else
				event.setNewSpeed( event.getNewSpeed() * ( 1.0f + data.getFloat( MINING_MULTIPLIER_TAG ) ) );
		}
	}

	/**
	 Calculating mining multiplier depending on ticks the player was holding left mouse.

	 @param player Player to calculate bonus on.

	 @return Returns multiplier which represents how fast the player will mine the block. (2.0f will mean twice as fast)
	 */
	protected static float getMiningMultiplier( PlayerEntity player ) {
		GottaMineFastEnchantment enchantment = Instances.GOTTA_MINE_FAST;
		CompoundNBT data = player.getPersistentData();

		return ( float )Math.pow(
			Math.min( data.getInt( COUNTER_TAG ), enchantment.maximumDuration.getDuration() ) / ( float )TimeConverter.minutesToTicks( 1.0 ),
			enchantment.exponent.get()
		);
	}

	public static class GottaMineFastMultiplier {
		private final float multiplier;

		public GottaMineFastMultiplier( float multiplier ) {
			this.multiplier = multiplier;
		}

		public GottaMineFastMultiplier( PacketBuffer buffer ) {
			this.multiplier = buffer.readFloat();
		}

		public void encode( PacketBuffer buffer ) {
			buffer.writeFloat( this.multiplier );
		}

		public void handle( Supplier< NetworkEvent.Context > contextSupplier ) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork( ()->{
				ServerPlayerEntity sender = context.getSender();
				if( sender == null )
					return;
				CompoundNBT data = sender.getPersistentData();
				data.putFloat( MINING_MULTIPLIER_TAG, this.multiplier );
			} );
			context.setPacketHandled( true );
		}
	}
}
