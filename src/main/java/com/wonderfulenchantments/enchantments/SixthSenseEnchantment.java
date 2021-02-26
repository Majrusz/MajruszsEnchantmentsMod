package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
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
		super( "sixth_sense", Rarity.RARE, EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD, "SixthSense" );

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
	public static void onMonsterTick( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity monster = event.getEntityLiving();
		if( !( event.getEntityLiving() instanceof MonsterEntity ) || event.getEntityLiving().world instanceof ServerWorld )
			return;

		CompoundNBT data = monster.getPersistentData();
		data.putInt( MONSTER_TAG, Math.max( data.getInt( MONSTER_TAG ) - 1, 0 ) );

		if( data.getInt( MONSTER_TAG ) == 1 )
			monster.setGlowing( false );
	}

	/** Updates sixth sense logic for given player. */
	private void update( PlayerEntity player ) {
		if( player.world instanceof ServerWorld )
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
	private void highlightNearbyEntities( PlayerEntity player ) {
		double x = player.getPosX(), y = player.getPosY(), z = player.getPosZ(), offset = this.offsetConfig.get();
		AxisAlignedBB axisAligned = new AxisAlignedBB( x - offset, y - offset, z - offset, x + offset, y + offset, z + offset );

		for( MonsterEntity monster : player.world.getEntitiesWithinAABB( MonsterEntity.class, axisAligned ) ) {
			CompoundNBT data = monster.getPersistentData();
			data.putInt( MONSTER_TAG, this.highlightDurationConfig.getDuration() );

			monster.setGlowing( true );
		}
	}

	/** Resets player's sixth sense standing still tick counter. */
	private void resetStandingStillCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		data.putInt( SENSE_TAG, 0 );
	}

	/** Increases by 1 player's sixth sense standing still tick counter. */
	private void increaseStandingStillCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		data.putInt( SENSE_TAG, data.getInt( SENSE_TAG ) + 1 );
	}

	/** Increases by 1 player's sixth sense tick counter. */
	private void increaseTickCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		data.putInt( TICK_TAG, data.getInt( TICK_TAG ) + 1 );
	}

	/** Returns current player's sixth sense standing still tick counter. */
	private int getStandingStillCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		return data.getInt( SENSE_TAG );
	}

	/** Returns current player's sixth sense tick counter. */
	private int getTickCounter( PlayerEntity player ) {
		CompoundNBT data = player.getPersistentData();
		return data.getInt( TICK_TAG );
	}

	/** Checks whether entities should be highlighted. */
	private boolean shouldHighlightEntities( PlayerEntity player ) {
		int currentStandingStillTicks = getStandingStillCounter( player );
		int currentTicks = getTickCounter( player );
		int preparingTimeInTicks = this.preparingTimeConfig.getDuration();
		int cooldownTicks = this.cooldownConfig.getDuration();

		return currentStandingStillTicks == preparingTimeInTicks || ( currentStandingStillTicks > preparingTimeInTicks && currentTicks % cooldownTicks == 0 );
	}

	/** Checks whether player moved since last tick. */
	private boolean isPlayerMoving( PlayerEntity player ) {
		return player.lastTickPosX != player.getPosX() || player.lastTickPosY != player.getPosY() || player.lastTickPosZ != player.getPosZ();
	}

	/** Checks whether player has Sixth Sense enchantment on its helmet. */
	private boolean hasEnchantment( PlayerEntity player ) {
		return EnchantmentHelper.getEnchantmentLevel( this, player.getItemStackFromSlot( EquipmentSlotType.HEAD ) ) > 0;
	}
}
