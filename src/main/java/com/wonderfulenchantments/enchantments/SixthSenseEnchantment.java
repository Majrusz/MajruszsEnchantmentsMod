package com.wonderfulenchantments.enchantments;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.mlib.MajruszLibrary;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Enchantment that highlights nearby entities when player is standing still. (inspired by PayDay2) */
@Mod.EventBusSubscriber
public class SixthSenseEnchantment extends WonderfulEnchantment {
	private static final String SENSE_TAG = "SixthSenseCounter";
	protected final DoubleConfig rangeConfig;
	protected final DurationConfig preparingTimeConfig;
	protected final DurationConfig cooldownConfig;

	public SixthSenseEnchantment() {
		super( Rarity.RARE, EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD, "SixthSense" );

		String rangeComment = "Maximum distance from player to entity.";
		String preparingComment = "Duration of standing still before the entities will be highlighted.";
		String cooldownComment = "Duration of standing still before the entities will be highlighted.";
		this.rangeConfig = new DoubleConfig( "range", rangeComment, false, 5.0, 1.0, 100.0 );
		this.preparingTimeConfig = new DurationConfig( "preparing_time", preparingComment, false, 3.5, 1.0, 60.0 );
		this.cooldownConfig = new DurationConfig( "cooldown", cooldownComment, false, 1.0, 0.1, 10.0 );
		this.enchantmentGroup.addConfigs( this.rangeConfig, this.preparingTimeConfig, this.cooldownConfig );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->12 );
	}

	@SubscribeEvent
	public static void onTick( TickEvent.PlayerTickEvent event ) {
		SixthSenseEnchantment sixthSense = Instances.SIXTH_SENSE;
		PlayerEntity player = event.player;

		if( player.world instanceof ServerWorld || event.phase == TickEvent.Phase.START )
			return;

		if( sixthSense.isPlayerMoving( player ) ) {
			sixthSense.increaseCounter( player );
		} else {
			sixthSense.resetCounter( player );
		}
		MajruszLibrary.LOGGER.debug( player.prevPosX + "->" + player.getPosX() + " (" + sixthSense.isPlayerMoving( player ) + ")" );

		// ResourceLocation resource = new ResourceLocation( "minecraft/test" );
	}

	/** Checks whether player moved since last tick. */
	private boolean isPlayerMoving( PlayerEntity player ) {
		return player.lastTickPosX != player.getPosX() || player.lastTickPosY != player.getPosY() || player.lastTickPosZ != player.getPosZ();
	}

	/** Resets player's sixth sense tick counter. */
	private void resetCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		data.putInt( SENSE_TAG, 0 );
	}

	/** Increases by 1 player's sixth sense tick counter. */
	private void increaseCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		data.putInt( SENSE_TAG, data.getInt( SENSE_TAG )+1 );
	}

	/** Returns current player's sixth sense tick counter. */
	private int getCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		return data.getInt( SENSE_TAG );
	}
}
