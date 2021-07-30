package com.wonderfulenchantments.enchantments;

import com.mlib.MajruszLibrary;
import com.mlib.LevelHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment which after successful hit attacks enemy with laser beam that cannot be dodged or blocked. */
@Mod.EventBusSubscriber
public class ElderGaurdianFavorEnchantment extends WonderfulEnchantment {
	private static final String LINK_TAG = "ElderGuardianFavorLinkedEntityID";
	private static final String LINK_COUNTER_TAG = "ElderGuardianFavorCounter";
	protected final DurationConfig beamCooldown;
	protected final DoubleConfig beamDamage;
	protected final DoubleConfig waterMultiplier;

	public ElderGaurdianFavorEnchantment() {
		super( "elder_guardian_favor", Rarity.RARE, EnchantmentCategory.TRIDENT, EquipmentSlot.MAINHAND, "ElderGuardianFavor" );
		String cooldown_comment = "Duration how long entities are linked before dealing damage.";
		String damage_comment = "Damage dealt by this enchantment.";
		String water_comment = "Damage multiplier when both entities are in water.";
		this.beamCooldown = new DurationConfig( "beam_cooldown", cooldown_comment, false, 4.0, 1.0, 60.0 );
		this.beamDamage = new DoubleConfig( "beam_damage", damage_comment, false, 6.0, 1.0, 100.0 );
		this.waterMultiplier = new DoubleConfig( "water_multiplier", water_comment, false, 1.5, 1.0, 10.0 );
		this.enchantmentGroup.addConfigs( this.beamCooldown, this.beamDamage, this.waterMultiplier );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 14 * level ) );
	}

	/** Event that links entities together on hit. */
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();

		if( !( damageSource.getEntity() instanceof LivingEntity ) || !( damageSource.getDirectEntity() instanceof LivingEntity ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getEntity();
		LivingEntity target = event.getEntityLiving();
		int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel( Instances.ELDER_GAURDIAN_FAVOR, attacker.getMainHandItem() );

		connectEntities( attacker, target, enchantmentLevel );
	}

	/** Event that updates link between entities and damage target after some time. */
	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity attacker = event.getEntityLiving();
		CompoundTag data = attacker.getPersistentData();
		int counter = data.getInt( LINK_COUNTER_TAG ) - 1;

		if( counter < 0 || !( attacker.level instanceof ServerLevel ) )
			return;

		data.putInt( LINK_COUNTER_TAG, counter );

		ElderGaurdianFavorEnchantment enchantment = Instances.ELDER_GAURDIAN_FAVOR;
		int targetID = data.getInt( LINK_TAG );
		ServerLevel world = ( ServerLevel )attacker.level;
		Entity targetEntity = world.getEntity( targetID );
		if( !( targetEntity instanceof LivingEntity ) )
			return;

		LivingEntity target = ( LivingEntity )targetEntity;
		if( counter > 0 ) {
			spawnParticles( attacker, target, world );
		} else {
			boolean areEntitiesInWater = ( target.isInWater() || LevelHelper.isEntityOutsideWhenItIsRaining( target )
			) && ( attacker.isInWater() || LevelHelper.isEntityOutsideWhenItIsRaining( attacker ) );

			world.playSound( null, target.getX(), target.getEyeY(), target.getZ(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT,
				0.5f, 1.8f
			);
			target.hurt( DamageSource.MAGIC,
				( float )( ( areEntitiesInWater ? enchantment.waterMultiplier.get() : 1.0 ) * enchantment.beamDamage.get() )
			);
		}
	}

	/**
	 Linking entities together.

	 @param attacker         Entity that attacks target.
	 @param target           Entity that was damaged and will be damaged second time later.
	 @param enchantmentLevel Attacker's level of 'Favor of Elder Guardian'.
	 */
	protected static void connectEntities( LivingEntity attacker, LivingEntity target, int enchantmentLevel ) {
		CompoundTag data = attacker.getPersistentData();

		if( data.getInt( LINK_COUNTER_TAG ) > 0 || enchantmentLevel == 0 )
			return;

		data.putInt( LINK_TAG, target.getId() );
		data.putInt( LINK_COUNTER_TAG, Instances.ELDER_GAURDIAN_FAVOR.beamCooldown.getDuration() );
	}

	/**
	 Spawning particles between entities when they are linked.

	 @param attacker Attacker.
	 @param target   Target.
	 @param world    Level at which particles will be spawned.
	 */
	protected static void spawnParticles( LivingEntity attacker, LivingEntity target, ServerLevel world ) {
		Vec3 difference = new Vec3( attacker.getX() - target.getX(), attacker.getY( 0.5 ) - target.getY( 0.5 ),
			attacker.getZ() - target.getZ()
		);
		Vec3 normalized = difference.normalize();
		double factor = 0.0;

		while( factor < difference.length() ) {
			double x = attacker.getX() - normalized.x * factor;
			double y = attacker.getY( 0.5 ) - normalized.y * factor;
			double z = attacker.getZ() - normalized.z * factor;
			world.sendParticles( ParticleTypes.BUBBLE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0 );
			world.sendParticles( ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0, 0.0, 0.0, 0.0 );

			factor += 1.8 - 0.8 + MajruszLibrary.RANDOM.nextDouble() * ( 1.7 - 0.8 );
		}
	}
}
