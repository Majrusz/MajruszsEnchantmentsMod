package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
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
public class ElderGaurdianFavorEnchantment extends Enchantment {
	protected static final String linkTag = "ElderGuardianFavorLinkedEntityID";
	protected static final String linkCounterTag = "ElderGuardianFavorCounter";

	public ElderGaurdianFavorEnchantment() {
		super( Rarity.RARE, EnchantmentType.TRIDENT, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 14 * level + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 20;
	}

	/** Event that links entities together on hit. */
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();

		if( !( damageSource.getTrueSource() instanceof LivingEntity ) || !( damageSource.getImmediateSource() instanceof LivingEntity ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getTrueSource();
		LivingEntity target = event.getEntityLiving();

		int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.ELDER_GUARDIAN_FAVOR.get(), attacker.getHeldItemMainhand() );
		connectEntities( attacker, target, enchantmentLevel );
	}

	/** Event that updates link between entities and damage target after some time. */
	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity attacker = event.getEntityLiving();
		CompoundNBT data = attacker.getPersistentData();
		int counter = data.getInt( linkCounterTag ) - 1;

		if( counter < 0 || !( attacker.world instanceof ServerWorld ) )
			return;

		data.putInt( linkCounterTag, counter );

		int targetID = data.getInt( linkTag );
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
				( float )( ( areEntitiesInWater ? 2.0 : 1.0 ) * ConfigHandler.Config.GUARDIAN_BEAM_DAMAGE.get() )
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

		if( data.getInt( linkCounterTag ) > 0 || enchantmentLevel == 0 )
			return;

		data.putInt( linkTag, target.getEntityId() );
		data.putInt( linkCounterTag, WonderfulEnchantmentHelper.secondsToTicks( ConfigHandler.Config.GUARDIAN_BEAM_DURATION.get() ) );
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
