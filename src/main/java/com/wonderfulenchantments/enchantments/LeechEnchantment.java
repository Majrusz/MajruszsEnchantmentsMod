package com.wonderfulenchantments.enchantments;

import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.damage.DamageHelper;
import com.mlib.effects.EffectHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

/** Enchantment that steals from enemy positive effect or some health. */
@Mod.EventBusSubscriber
public class LeechEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig leechChance;
	protected final DurationConfig maximumDuration;

	public LeechEnchantment() {
		super( "leech", Rarity.UNCOMMON, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, "Leech" );
		String chanceComment = "Chance for stealing positive effect/health from enemy.";
		String durationComment = "Maximum duration in seconds that effect can have.";
		this.leechChance = new DoubleConfig( "leech_chance", chanceComment, false, 0.25, 0.0, 1.0 );
		this.maximumDuration = new DurationConfig( "maximum_duration", durationComment, false, 120.0, 0.1, 600.0 );
		this.enchantmentGroup.addConfigs( this.leechChance, this.maximumDuration );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( level * 20 ) );
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canApply( stack );
	}

	/** Event that applies enchantment effect when attacker has an appropriate enchantment level and if 'leeching' succeeded. */
	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		if( !DamageHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		LeechEnchantment enchantment = Instances.LEECH;

		if( !Random.tryChance( enchantment.leechChance.get() ) )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource()
			.getImmediateSource();
		LivingEntity target = event.getEntityLiving();

		int vampirismLevel = 0;
		if( attacker != null )
			vampirismLevel = EnchantmentHelperPlus.calculateEnchantmentSum( enchantment, attacker.getArmorInventoryList() );

		if( EnchantmentHelper.getMaxEnchantmentLevel( enchantment, attacker ) > 0 ) {
			for( int i = 0; i < 1 + vampirismLevel; i++ )
				steal( attacker, target );
			spawnParticlesAndPlaySounds( attacker, target );
		}
	}

	/**
	 Stealing from the entity first positive effect. If entity does not have any, then stealing health.

	 @param stealer Entity which will receive some bonuses.
	 @param target  Entity which will lose something.
	 */
	protected static void steal( LivingEntity stealer, LivingEntity target ) {
		Collection< EffectInstance > targetEffects = target.getActivePotionEffects();

		if( !( targetEffects.size() > 0 && stealEffect( stealer, target, targetEffects ) ) )
			stealHealth( stealer, target );
	}

	/**
	 Stealing from the entity first positive effect.

	 @param stealer Entity which will receive new effect.
	 @param target  Entity which will lose the effect.
	 @param effects Collection of all target effects.

	 @return Returns whether stealing effect succeeded. May be false when target has only negative effects.
	 */
	protected static boolean stealEffect( LivingEntity stealer, LivingEntity target, Collection< EffectInstance > effects ) {
		EffectInstance[] possibleEffects = effects.toArray( new EffectInstance[]{} );

		for( EffectInstance effect : possibleEffects )
			if( effect.getPotion()
				.isBeneficial() ) {
				int maximumDurationInTicks = Math.min( effect.getDuration(), Instances.LEECH.maximumDuration.getDuration() );
				EffectHelper.applyEffectIfPossible( stealer, effect.getPotion(), maximumDurationInTicks, effect.getAmplifier() );
				target.removePotionEffect( effect.getPotion() );

				return true;
			}

		return false;
	}

	/**
	 Stealing from the entity fixed amount of the health.

	 @param stealer Entity which will receive some extra health.
	 @param target  Entity which will be damage from magic source.
	 */
	protected static void stealHealth( LivingEntity stealer, LivingEntity target ) {
		target.attackEntityFrom( DamageSource.MAGIC, 1.0f );
		stealer.heal( 1.0f );
	}

	/**
	 Spawning particles between entities and playing sound. The order does not matter.

	 @param attacker One of two entities.
	 @param target   One of two entities.
	 */
	protected static void spawnParticlesAndPlaySounds( LivingEntity attacker, LivingEntity target ) {
		if( !( attacker.getEntityWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )attacker.getEntityWorld();

		Vector3d startPosition = attacker.getPositionVec()
			.add( new Vector3d( 0.0D, attacker.getHeight() * 0.75D, 0.0D ) );
		Vector3d endPosition = target.getPositionVec()
			.add( new Vector3d( 0.0D, target.getHeight() * 0.75D, 0.0D ) );

		Vector3d difference = endPosition.subtract( startPosition );
		int amountOfParticles = ( int )( Math.ceil( startPosition.distanceTo( endPosition ) * 5.0D ) );

		for( int i = 0; i <= amountOfParticles; i++ ) {
			Vector3d step = difference.scale( ( float )( i ) / amountOfParticles );
			Vector3d finalPosition = startPosition.add( step );
			world.spawnParticle( ParticleTypes.ENCHANTED_HIT, finalPosition.getX(), finalPosition.getY(), finalPosition.getZ(), 1, 0.0D, 0.0D, 0.0D,
				0.0D
			);
		}

		world.playSound( null, startPosition.getX(), startPosition.getY(), startPosition.getZ(), SoundEvents.ENTITY_GENERIC_DRINK,
			SoundCategory.AMBIENT, 0.25F, 1.0F
		);
	}
}
