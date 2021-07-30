package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that highlights nearby entities when player is standing still. (inspired by PayDay2) */
@Mod.EventBusSubscriber
public class SixthSenseEnchantment extends WonderfulEnchantment {
	private static final String SENSE_TAG = "SixthSenseCounter";
	private static final String TICK_TAG = "SixthSenseTickCounter";
	private static final String MONSTER_TAG = "SixthSenseHighlightTicksLeft";
	protected final DoubleConfig offsetConfig;
	protected final DurationConfig preparingTimeConfig;
	protected final DurationConfig cooldownConfig;
	protected final DurationConfig highlightDurationConfig;

	public SixthSenseEnchantment() {
		super( "sixth_sense", Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD, "SixthSense" );

		String offsetComment = "Maximum distance in blocks from player to entity.";
		String preparingComment = "Duration of standing still before the entities will be highlighted.";
		String cooldownComment = "Duration between calculating entities to glow.";
		String highlightComment = "Duration of entities being highlighted.";
		this.offsetConfig = new DoubleConfig( "offset", offsetComment, false, 15.0, 1.0, 100.0 );
		this.preparingTimeConfig = new DurationConfig( "preparing_time", preparingComment, false, 3.5, 1.0, 60.0 );
		this.cooldownConfig = new DurationConfig( "cooldown", cooldownComment, false, 0.5, 0.1, 10.0 );
		this.highlightDurationConfig = new DurationConfig( "highlight_duration", highlightComment, false, 5.0, 0.5, 60.0 );
		this.enchantmentGroup.addConfigs( this.offsetConfig, this.preparingTimeConfig, this.cooldownConfig, this.highlightDurationConfig );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->12 );
	}

	@SubscribeEvent
	public static void onPlayerTick( TickEvent.PlayerTickEvent event ) {
		if( event.phase != TickEvent.Phase.START )
			Instances.SIXTH_SENSE.update( event.player );
	}

	@SubscribeEvent
	public static void onEntityTick( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity livingEntity = event.getEntityLiving();
		if( event.getEntityLiving().level instanceof ServerLevel )
			return;

		CompoundTag data = livingEntity.getPersistentData();
		data.putInt( MONSTER_TAG, Math.max( data.getInt( MONSTER_TAG ) - 1, 0 ) );

		if( data.getInt( MONSTER_TAG ) == 1 )
			livingEntity.setGlowingTag( false );
	}

	/** Updates sixth sense logic for given player. */
	private void update( Player player ) {
		if( player.level instanceof ServerLevel )
			return;

		increaseTickCounter( player );
		if( !isPlayerMoving( player ) )
			increaseStandingStillCounter( player );
		else
			resetStandingStillCounter( player );

		if( hasEnchantment( player ) && shouldHighlightEntities( player ) )
			highlightNearbyEntities( player );
	}

	/** Highlights nearby entities in certain range. */
	private void highlightNearbyEntities( Player player ) {
		double x = player.getX(), y = player.getY(), z = player.getZ(), offset = this.offsetConfig.get();
		AABB axisAligned = new AABB( x - offset, y - offset, z - offset, x + offset, y + offset, z + offset );

		for( LivingEntity livingEntity : player.level.getEntitiesOfClass( LivingEntity.class, axisAligned ) ) {
			if( livingEntity == player )
				continue;

			CompoundTag data = livingEntity.getPersistentData();
			data.putInt( MONSTER_TAG, this.highlightDurationConfig.getDuration() );

			livingEntity.setGlowingTag( true );
		}
	}

	/** Resets player's sixth sense standing still tick counter. */
	private void resetStandingStillCounter( Player player ) {
		CompoundTag data = player.getPersistentData();
		data.putInt( SENSE_TAG, 0 );
	}

	/** Increases by 1 player's sixth sense standing still tick counter. */
	private void increaseStandingStillCounter( Player player ) {
		CompoundTag data = player.getPersistentData();
		data.putInt( SENSE_TAG, data.getInt( SENSE_TAG ) + 1 );
	}

	/** Increases by 1 player's sixth sense tick counter. */
	private void increaseTickCounter( Player player ) {
		CompoundTag data = player.getPersistentData();
		data.putInt( TICK_TAG, data.getInt( TICK_TAG ) + 1 );
	}

	/** Returns current player's sixth sense standing still tick counter. */
	private int getStandingStillCounter( Player player ) {
		CompoundTag data = player.getPersistentData();
		return data.getInt( SENSE_TAG );
	}

	/** Returns current player's sixth sense tick counter. */
	private int getTickCounter( Player player ) {
		CompoundTag data = player.getPersistentData();
		return data.getInt( TICK_TAG );
	}

	/** Checks whether entities should be highlighted. */
	private boolean shouldHighlightEntities( Player player ) {
		int currentStandingStillTicks = getStandingStillCounter( player );
		int currentTicks = getTickCounter( player );
		int preparingTimeInTicks = this.preparingTimeConfig.getDuration();
		int cooldownTicks = this.cooldownConfig.getDuration();

		return currentStandingStillTicks == preparingTimeInTicks || ( currentStandingStillTicks > preparingTimeInTicks && currentTicks % cooldownTicks == 0 );
	}

	/** Checks whether player moved since last tick. */
	private boolean isPlayerMoving( Player player ) {
		return player.xo != player.getX() || player.yo != player.getY() || player.zo != player.getZ();
	}

	/** Checks whether player has Sixth Sense enchantment on its helmet. */
	private boolean hasEnchantment( Player player ) {
		return EnchantmentHelper.getItemEnchantmentLevel( this, player.getItemBySlot( EquipmentSlot.HEAD ) ) > 0;
	}
}
