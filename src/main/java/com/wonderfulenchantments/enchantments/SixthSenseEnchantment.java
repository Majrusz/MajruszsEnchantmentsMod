package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.math.AABBHelper;
import com.mlib.nbt.NBTHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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
		this.offsetConfig = new DoubleConfig( "offset", offsetComment, false, 15.0, 1.0, 100.0 );

		String preparingComment = "Duration of standing still before the entities will be highlighted.";
		this.preparingTimeConfig = new DurationConfig( "preparing_time", preparingComment, false, 3.5, 1.0, 60.0 );

		String cooldownComment = "Duration between calculating entities to glow.";
		this.cooldownConfig = new DurationConfig( "cooldown", cooldownComment, false, 0.5, 0.1, 10.0 );

		String highlightComment = "Duration of entities being highlighted.";
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
		if( livingEntity.level instanceof ServerLevel )
			return;

		NBTHelper.IntegerData monsterTagData = new NBTHelper.IntegerData( livingEntity, MONSTER_TAG );
		monsterTagData.set( value->Math.max( value - 1, 0 ) );
		if( monsterTagData.get() == 1 )
			livingEntity.setGlowingTag( false );
	}

	/** Updates sixth sense's logic for a given player. */
	private void update( Player player ) {
		if( player.level instanceof ServerLevel )
			return;

		NBTHelper.IntegerData senseTagData = new NBTHelper.IntegerData( player, SENSE_TAG );
		NBTHelper.IntegerData tickTagData = new NBTHelper.IntegerData( player, TICK_TAG );
		tickTagData.set( value->value + 1 );
		senseTagData.set( isPlayerMoving( player ) ? value->0 : value->value + 1 );

		if( hasEnchantment( player.getItemBySlot( EquipmentSlot.HEAD ) ) && shouldHighlightEntities( senseTagData, tickTagData ) )
			highlightNearbyEntities( player );
	}

	/** Highlights nearby entities in certain range. */
	private void highlightNearbyEntities( Player player ) {
		AABB axisAligned = AABBHelper.createInflatedAABB( player.position(), this.offsetConfig.get() );

		for( LivingEntity livingEntity : player.level.getEntitiesOfClass( LivingEntity.class, axisAligned ) ) {
			if( livingEntity == player )
				continue;

			NBTHelper.IntegerData monsterTagData = new NBTHelper.IntegerData( livingEntity, MONSTER_TAG );
			monsterTagData.set( this.highlightDurationConfig.getDuration() );
			livingEntity.setGlowingTag( true );
		}
	}

	/** Checks whether entities should be highlighted. */
	private boolean shouldHighlightEntities( NBTHelper.IntegerData senseTagData, NBTHelper.IntegerData tickTagData ) {
		int currentStandingStillTicks = senseTagData.get();
		int currentTicks = tickTagData.get();
		int preparingTimeInTicks = this.preparingTimeConfig.getDuration();
		int cooldownTicks = this.cooldownConfig.getDuration();

		return currentStandingStillTicks == preparingTimeInTicks || ( currentStandingStillTicks > preparingTimeInTicks && currentTicks % cooldownTicks == 0 );
	}

	/** Checks whether player moved since last tick. */
	private boolean isPlayerMoving( Player player ) {
		return player.xo != player.getX() || player.yo != player.getY() || player.zo != player.getZ();
	}
}
