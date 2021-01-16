package com.wonderfulenchantments.enchantments;

import com.mlib.TimeConverter;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.ConfigHandlerOld;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment which after successful hit attacks enemy with laser beam that cannot be dodged or blocked. */
@Mod.EventBusSubscriber
public class ElderGaurdianFavorEnchantment extends WonderfulEnchantment {
	private static final String LINK_TAG = "ElderGuardianFavorLinkedEntityID";
	private static final String LINK_COUNTER_TAG = "ElderGuardianFavorCounter";
	protected final DurationConfig beamCooldown;
	protected final DoubleConfig beamDamage;
	protected final DoubleConfig waterMultiplier;

	public ElderGaurdianFavorEnchantment() {
		super( Rarity.RARE, EnchantmentType.TRIDENT, EquipmentSlotType.MAINHAND, "ElderGuardianFavor" );
		String cooldown_comment = "Duration how long entities are linked before dealing damage.";
		String damage_comment = "Damage dealt by this enchantment.";
		String water_comment = "Damage multiplier when both entities are in water.";
		this.beamCooldown = new DurationConfig( "beam_cooldown", cooldown_comment, false, 3.0, 1.0, 60.0 );
		this.beamDamage = new DoubleConfig( "beam_damage", damage_comment, false, 5.0, 1.0, 100.0 );
		this.waterMultiplier = new DoubleConfig( "water_multiplier", water_comment, false, 2.0, 1.0, 10.0 );
		this.enchantmentGroup.addConfigs( this.beamCooldown, this.beamDamage, this.waterMultiplier );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 14 * level ) );
	}

	/** Event that links entities together on hit. */
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();

		if( !( damageSource.getTrueSource() instanceof LivingEntity ) || !( damageSource.getImmediateSource() instanceof LivingEntity ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getTrueSource();
		LivingEntity target = event.getEntityLiving();
		int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( Instances.ELDER_GAURDIAN_FAVOR, attacker.getHeldItemMainhand() );

		connectEntities( attacker, target, enchantmentLevel );
	}

	/** Event that updates link between entities and damage target after some time. */
	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity attacker = event.getEntityLiving();
		CompoundNBT data = attacker.getPersistentData();
		int counter = data.getInt( LINK_COUNTER_TAG ) - 1;

		if( counter < 0 || !( attacker.world instanceof ServerWorld ) )
			return;

		data.putInt( LINK_COUNTER_TAG, counter );

		ElderGaurdianFavorEnchantment enchantment = Instances.ELDER_GAURDIAN_FAVOR;
		int targetID = data.getInt( LINK_TAG );
		ServerWorld world = ( ServerWorld )attacker.world;
		Entity targetEntity = world.getEntityByID( targetID );
		if( !( targetEntity instanceof LivingEntity ) )
			return;

		LivingEntity target = ( LivingEntity )targetEntity;
		if( counter > 0 ) {
			spawnParticles( attacker, target, world );
		} else {
			boolean areEntitiesInWater = target.isInWater() && attacker.isInWater();

			world.playSound( null, target.getPosX(), target.getPosYEye(), target.getPosZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.AMBIENT,
				0.5f, 1.8f
			);
			target.attackEntityFrom( DamageSource.MAGIC,
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
		CompoundNBT data = attacker.getPersistentData();

		if( data.getInt( LINK_COUNTER_TAG ) > 0 || enchantmentLevel == 0 )
			return;

		data.putInt( LINK_TAG, target.getEntityId() );
		data.putInt( LINK_COUNTER_TAG, Instances.ELDER_GAURDIAN_FAVOR.beamCooldown.getDuration() );
	}

	/**
	 Spawning particles between entities when they are linked.

	 @param attacker Attacker.
	 @param target   Target.
	 @param world    World at which particles will be spawned.
	 */
	protected static void spawnParticles( LivingEntity attacker, LivingEntity target, ServerWorld world ) {
		Vector3d difference = new Vector3d( attacker.getPosX() - target.getPosX(), attacker.getPosYHeight( 0.5 ) - target.getPosYHeight( 0.5 ),
			attacker.getPosZ() - target.getPosZ()
		);
		Vector3d normalized = difference.normalize();
		double factor = 0.0;

		while( factor < difference.length() ) {
			double x = attacker.getPosX() - normalized.x * factor;
			double y = attacker.getPosYHeight( 0.5 ) - normalized.y * factor;
			double z = attacker.getPosZ() - normalized.z * factor;
			world.spawnParticle( ParticleTypes.BUBBLE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0 );
			world.spawnParticle( ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0, 0.0, 0.0, 0.0 );

			factor += 1.8 - 0.8 + WonderfulEnchantments.RANDOM.nextDouble() * ( 1.7 - 0.8 );
		}
	}
}
