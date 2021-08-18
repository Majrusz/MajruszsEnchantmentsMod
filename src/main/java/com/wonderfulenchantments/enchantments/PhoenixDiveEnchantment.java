package com.wonderfulenchantments.enchantments;

import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Predicate;

/** Enchantment that releases fire wave when entity falls. (inspired by Divinity: Original Sin 2) */
@Mod.EventBusSubscriber
public class PhoenixDiveEnchantment extends WonderfulEnchantment {
	private static final String FOOT_PARTICLE_TAG = "PhoenixDiveFootParticleTick";
	protected final DoubleConfig jumpMultiplier, damageDistance;
	protected final IntegerConfig jumpPenalty;

	public PhoenixDiveEnchantment() {
		super( "phoenix_dive", Rarity.RARE, EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET, "PheonixDive" );

		String jumpComment = "Jumping power multiplier per enchantment level.";
		this.jumpMultiplier = new DoubleConfig( "jump_multiplier", jumpComment, false, 0.25, 0.01, 1.0 );

		String distanceComment = "Area of entities that will take damage. (area of square where A = (x - value, z - value) and B = (x + value, z + value))";
		this.damageDistance = new DoubleConfig( "damage_range", distanceComment, false, 5.0, 1.0, 100.0 );

		String penaltyComment = "Penalty for using special jump. (damage to durability)";
		this.jumpPenalty = new IntegerConfig( "jump_penalty", penaltyComment, false, 3, 0, 100 );

		this.enchantmentGroup.addConfigs( this.jumpMultiplier, this.jumpPenalty, this.damageDistance );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 10 * ( level + 1 ) ) );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof FrostWalkerEnchantment ) && super.checkCompatibility( enchantment );
	}

	/** Event that will leave fire wave when the entity falls from certain height. */
	@SubscribeEvent
	public static void onFall( LivingFallEvent event ) {
		if( event.getDistance() <= 3.0 )
			return;

		LivingEntity attacker = event.getEntityLiving();
		int enchantmentLevel = getPhoenixDiveLevel( attacker );

		if( enchantmentLevel <= 0 || !( attacker.level instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )attacker.level;
		for( Entity entity : getEntitiesInRange( attacker, world ) )
			if( entity instanceof LivingEntity ) {
				LivingEntity target = ( LivingEntity )entity;
				target.setSecondsOnFire( 3 * enchantmentLevel );
				target.hurt( DamageSource.explosion( attacker ), 0 );
				target.hurt( DamageSource.ON_FIRE, ( float )Math.sqrt( enchantmentLevel * event.getDistance() ) );
			}

		spawnFallParticles( attacker.position(), world );
	}

	/** Event that will create particles on players that have enchantment on their boots. */
	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		Player player = event.player;
		CompoundTag data = player.getPersistentData();
		if( getPhoenixDiveLevel( player ) <= 0 || !( player.level instanceof ServerLevel ) )
			return;

		int ticks = data.getInt( FOOT_PARTICLE_TAG );

		if( ticks % 3 == 0 )
			spawnFootParticle( player, ( ServerLevel )player.level, ticks % 6 == 0 );

		ticks++;

		if( ticks >= 6 )
			ticks = 0;

		data.putInt( FOOT_PARTICLE_TAG, ticks );
	}

	/** Event that increases jump height when player is holding sneak key. */
	@SubscribeEvent
	public static void onJump( LivingEvent.LivingJumpEvent event ) {
		if( !( event.getEntityLiving() instanceof Player ) )
			return;

		Player player = ( Player )event.getEntityLiving();
		ItemStack boots = player.getItemBySlot( EquipmentSlot.FEET );
		int enchantmentLevel = getPhoenixDiveLevel( player );

		if( !player.isCrouching() || enchantmentLevel <= 0 )
			return;

		double angleInRadians = Math.toRadians( player.yBodyRot + 90.0 );
		double factor = ( enchantmentLevel + 1 ) * Instances.PHOENIX_DIVE.jumpMultiplier.get();
		player.setDeltaMovement( player.getDeltaMovement()
			.multiply( new Vec3( 0.0, 1.0 + factor, 0.0 ) )
			.add( factor * Math.cos( angleInRadians ), 0.0, factor * Math.sin( angleInRadians ) ) );

		int damagePenalty = Instances.PHOENIX_DIVE.jumpPenalty.get();
		if( damagePenalty > 0 )
			boots.hurtAndBreak( damagePenalty, player, entity->entity.broadcastBreakEvent( EquipmentSlot.FEET ) );

		if( !( player.level instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )player.level;
		world.playSound( null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, 0.5f, 0.9f );
	}

	/**
	 Returning Phoenix Dive enchantment level.

	 @param entity Entity to check level.
	 */
	protected static int getPhoenixDiveLevel( LivingEntity entity ) {
		return EnchantmentHelper.getItemEnchantmentLevel( Instances.PHOENIX_DIVE, entity.getItemBySlot( EquipmentSlot.FEET ) );
	}

	/**
	 Getting entities in certain range.

	 @param livingEntity Entity as a start position.
	 @param world        Current entity world.

	 @return Returns list with entities that were in range.
	 */
	protected static List< Entity > getEntitiesInRange( LivingEntity livingEntity, ServerLevel world ) {
		double range = Instances.PHOENIX_DIVE.damageDistance.get();
		List< Entity > entities = world.getEntities( livingEntity, livingEntity.getBoundingBox()
			.inflate( range, livingEntity.getBbHeight(), range ) );

		return entities.stream()
			.filter( getEntitiesPredicate( livingEntity ) )
			.toList();
	}

	/** Returns predicate to check whether given entity is valid target. */
	protected static Predicate< Entity > getEntitiesPredicate( LivingEntity livingEntity ) {
		return entity->{
			boolean canAttack = entity instanceof LivingEntity && AttributeHandler.hasAttribute( ( LivingEntity )entity, Attributes.ATTACK_DAMAGE );
			boolean isTamedByEntity = entity instanceof TamableAnimal && ( ( TamableAnimal )entity ).isOwnedBy( livingEntity );
			boolean isTargetedByEntity = livingEntity.getLastHurtByMob() != null && livingEntity.getLastHurtByMob()
				.is( entity );
			boolean wasAttackedByEntity = livingEntity.getLastHurtMob() != null && livingEntity.getLastHurtMob()
				.is( entity );

			return canAttack && !isTamedByEntity || isTargetedByEntity || wasAttackedByEntity;
		};
	}

	/**
	 Spawning particles on fall.

	 @param position Position where the entity landed.
	 @param world    Level where particles should be spawned.
	 */
	protected static void spawnFallParticles( Vec3 position, ServerLevel world ) {
		double x = position.x, y = position.y, z = position.z;
		for( double d = 0.0; d < 3.0; d++ )
			world.sendParticles( RegistryHandler.PHOENIX_PARTICLE.get(), x, y, z, ( int )Math.pow( 5.0, d + 1.0 ), 0.0625, 0.125, 0.0625,
				( 0.125 + 0.0625 ) * ( d + 1.0 )
			);

		world.playSound( null, x, y, z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, 0.5f, 0.9f );
	}

	/**
	 Spawning particles at foot height.

	 @param entity    Entity where the particles will be spawned.
	 @param world     Level where particles should be spawned.
	 @param isLeftLeg Flag that informs to spawn particle at left leg position.
	 */
	protected static void spawnFootParticle( LivingEntity entity, ServerLevel world, boolean isLeftLeg ) {
		if( entity.isFallFlying() )
			return;

		double leftLegRotation = ( isLeftLeg ? 180.0 : 0.0 );
		double angleInRadians = Math.toRadians( entity.yBodyRot + 90.0 + leftLegRotation );
		world.sendParticles( ParticleTypes.FLAME, entity.getX() + 0.1875 * Math.sin( -angleInRadians ), entity.getY() + 0.1,
			entity.getZ() + 0.1875 * Math.cos( -angleInRadians ), 1, 0.0, 0.125 * Math.cos( angleInRadians ), 0.00, 0.0
		);
	}
}
