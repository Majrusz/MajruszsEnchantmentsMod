package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.entities.EntityHelper;
import com.mlib.math.AABBHelper;
import com.mlib.math.VectorHelper;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/** Enchantment that redirects some of the damage from player's animals and villagers to the player. */
@Mod.EventBusSubscriber
public class GuardianEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig redirectMultiplier;
	protected final DoubleConfig minimumHealthRatio;
	protected final DoubleConfig minimumDamageToRedirect;
	protected final DoubleConfig redirectDistance;
	protected final IntegerConfig maximumAmplifier;
	protected final DurationConfig effectDuration;
	protected static final MobEffect RESISTANCE_EFFECT = MobEffects.DAMAGE_RESISTANCE;

	public GuardianEnchantment() {
		super( "guardian_angel", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlots.BOTH_HANDS, "GuardianAngel" );

		String redirectComment = "Amount of damage redirected to the player. (in percentage)";
		this.redirectMultiplier = new DoubleConfig( "redirect_multiplier", redirectComment, false, 0.25, 0.01, 1.0 );

		String ratioComment = "Minimum player's health ratio to redirect damage to the player.";
		this.minimumHealthRatio = new DoubleConfig( "minimum_health_ratio", ratioComment, false, 0.5, 0.01, 1.0 );

		String damageComment = "Minimum damage required to redirect damage to any entity.";
		this.minimumDamageToRedirect = new DoubleConfig( "minimum_damage", damageComment, false, 2.0, 2.0, 100.0 );

		String distanceComment = "Maximum distance to the entity to redirect damage.";
		this.redirectDistance = new DoubleConfig( "redirect_distance", distanceComment, false, 10.0, 1.0, 100.0 );

		String amplifierComment = "Maximum amplifier of 'Resistance' effect.";
		this.maximumAmplifier = new IntegerConfig( "maximum_amplifier", amplifierComment, false, 2, 0, 4 );

		String durationComment = "Duration of 'Resistance' effect.";
		this.effectDuration = new DurationConfig( "effect_duration", durationComment, false, 12.0, 0.0, 600.0 );

		this.enchantmentGroup.addConfigs( this.redirectMultiplier, this.minimumHealthRatio, this.minimumDamageToRedirect, this.redirectDistance, this.maximumAmplifier, this.effectDuration );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 38 );
		setMinimumEnchantabilityCalculator( level->12 );
	}

	@SubscribeEvent
	public static void onEntityAttacked( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();
		DamageSource damageSource = event.getSource();
		GuardianEnchantment guardian = Instances.GUARDIAN;
		ServerLevel level = CommonHelper.castIfPossible( ServerLevel.class, target.level );
		if( level == null )
			return;

		double redirectMultiplier = guardian.redirectMultiplier.get();
		for( LivingEntity nearbyGuardian : guardian.getNearbyGuardians( level, damageSource, target ) ) {
			float damage = event.getAmount();
			if( damage < guardian.minimumDamageToRedirect.get() )
				break;

			if( !guardian.hasEnchantment( target ) ) {
				nearbyGuardian.hurt( damageSource, ( float )( damage * redirectMultiplier ) );
				event.setAmount( ( float )( damage * ( 1.0f - redirectMultiplier ) ) );
			}
			guardian.applyResistance( nearbyGuardian );
			guardian.applyResistance( target );
		}
	}

	/** Returns list of entities that can absorb damage from the target. */
	protected List< LivingEntity > getNearbyGuardians( ServerLevel level, DamageSource source, LivingEntity target ) {
		double maximumRedirectDistance = this.redirectDistance.get();
		AABB axisAligned = AABBHelper.createInflatedAABB( target.position(), maximumRedirectDistance );
		Comparator< LivingEntity > comparator = ( e1, e2 )->e1.distanceTo( target ) < e2.distanceTo( target ) ? -1 : 1;

		List< LivingEntity > nearbyGuardians = level.getEntitiesOfClass( LivingEntity.class, axisAligned, getGuardianPredicate( source, target ) );
		nearbyGuardians.sort( comparator );
		return nearbyGuardians;
	}

	/** Returns guardian entity predicate. */
	protected Predicate< LivingEntity > getGuardianPredicate( DamageSource damageSource, LivingEntity target ) {
		Predicate< LivingEntity > distancePredicate = entity->VectorHelper.distance( entity.position(),
			target.position()
		) <= this.redirectDistance.get();
		Predicate< LivingEntity > friendPredicate = entity->isTargetFriendly( target, entity );
		Predicate< LivingEntity > healthPredicate = entity->EntityHelper.getHealthRatio( entity ) >= this.minimumHealthRatio.get();
		Predicate< LivingEntity > differentPredicate = entity->!entity.equals( damageSource.getEntity() );
		Predicate< LivingEntity > hasGuardianPredicate = this::hasEnchantment;

		return distancePredicate.and( friendPredicate.and( healthPredicate.and( differentPredicate.and( hasGuardianPredicate ) ) ) );
	}

	/** Returns whether target and nearby entity are valid. (to redirect damage) */
	protected boolean isTargetFriendly( LivingEntity target, LivingEntity nearbyEntity ) {
		TamableAnimal animal = CommonHelper.castIfPossible( TamableAnimal.class, target );

		return ( animal != null && animal.getOwnerUUID() == nearbyEntity.getUUID() ) || target instanceof Villager;
	}

	/** Applies next resistance effect to given entity. (or increase amplifier if it has one) */
	protected void applyResistance( LivingEntity entity ) {
		EffectHelper.applyEffectIfPossible( entity, RESISTANCE_EFFECT, this.effectDuration.getDuration(), getResistanceAmplifier( entity ) );
	}

	/** Calculates new resistance amplifier. */
	protected int getResistanceAmplifier( LivingEntity entity ) {
		if( entity.hasEffect( RESISTANCE_EFFECT ) ) {
			MobEffectInstance effectInstance = entity.getEffect( RESISTANCE_EFFECT );
			if( effectInstance != null )
				return Math.min( effectInstance.getAmplifier() + 1, this.maximumAmplifier.get() );
		}

		return 0;
	}
}
