package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.math.VectorHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
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
		super( "leech", Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, "Leech" );

		String chanceComment = "Chance for stealing positive effect/health from enemy.";
		this.leechChance = new DoubleConfig( "leech_chance", chanceComment, false, 0.25, 0.0, 1.0 );

		String durationComment = "Maximum duration in seconds that effect can have.";
		this.maximumDuration = new DurationConfig( "maximum_duration", durationComment, false, 120.0, 0.1, 600.0 );

		this.enchantmentGroup.addConfigs( this.leechChance, this.maximumDuration );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( level * 20 ) );
	}

	@Override
	public boolean canEnchant( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canEnchant( stack );
	}

	/** Event that applies enchantment effect when attacker has an appropriate enchantment level and if 'leech' succeeded. */
	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		LeechEnchantment leech = Instances.LEECH;
		if( !Random.tryChance( leech.leechChance.get() ) )
			return;

		DamageSource damageSource = event.getSource();
		LivingEntity attacker = CommonHelper.castIfPossible( LivingEntity.class, damageSource.getDirectEntity() );
		LivingEntity target = event.getEntityLiving();
		if( attacker == null )
			return;

		if( leech.hasEnchantment( attacker ) ) {
			int vampirismLevel = leech.getEnchantmentSum( attacker.getArmorSlots() );
			for( int i = 0; i < 1 + vampirismLevel; i++ )
				leech.steal( attacker, target );
			spawnParticlesAndPlaySounds( attacker, target );
		}
	}

	/** Spawns particles between entities and plays sound. The order does not matter. */
	protected static void spawnParticlesAndPlaySounds( LivingEntity attacker, LivingEntity target ) {
		ServerLevel world = CommonHelper.castIfPossible( ServerLevel.class, attacker.level );
		if( world == null )
			return;

		Vec3 startPosition = VectorHelper.add( attacker.position(), new Vec3( 0.0, attacker.getBbHeight() * 0.75, 0.0 ) );
		Vec3 endPosition = VectorHelper.add( target.position(), new Vec3( 0.0, target.getBbHeight() * 0.75, 0.0 ) );
		Vec3 difference = VectorHelper.subtract( endPosition, startPosition );
		int amountOfParticles = ( int )( Math.ceil( startPosition.distanceTo( endPosition ) * 5.0 ) );

		for( int i = 0; i <= amountOfParticles; i++ ) {
			Vec3 step = VectorHelper.multiply( difference, ( float )( i ) / amountOfParticles );
			Vec3 currentPosition = VectorHelper.add( startPosition, step );
			world.sendParticles( ParticleTypes.ENCHANTED_HIT, currentPosition.x, currentPosition.y, currentPosition.z, 1, 0.0, 0.0, 0.0, 0.0 );
		}

		world.playSound( null, startPosition.x, startPosition.y, startPosition.z, SoundEvents.GENERIC_DRINK, SoundSource.AMBIENT, 0.25F, 1.0F );
	}

	/** Steals from the entity the first positive effect. If entity does not have any, then it steals health. */
	protected void steal( LivingEntity stealer, LivingEntity target ) {
		Collection< MobEffectInstance > targetEffects = target.getActiveEffects();

		if( !( targetEffects.size() > 0 && stealEffect( stealer, target, targetEffects ) ) )
			stealHealth( stealer, target );
	}

	/**
	 Steals from the entity the first positive effect.

	 @return Returns whether stealing effect succeeded. May be false when target has only negative effects or does not have any effects.
	 */
	protected boolean stealEffect( LivingEntity stealer, LivingEntity target, Collection< MobEffectInstance > effects ) {
		MobEffectInstance[] possibleEffects = effects.toArray( new MobEffectInstance[]{} );

		for( MobEffectInstance effectInstance : possibleEffects ) {
			MobEffect effect = effectInstance.getEffect();
			if( effect.isBeneficial() ) {
				int maximumDurationInTicks = Math.min( effectInstance.getDuration(), this.maximumDuration.getDuration() );
				EffectHelper.applyEffectIfPossible( stealer, effect, maximumDurationInTicks, effectInstance.getAmplifier() );
				target.removeEffect( effect );

				return true;
			}
		}

		return false;
	}

	/** Steals from the entity fixed amount of health. */
	protected void stealHealth( LivingEntity stealer, LivingEntity target ) {
		target.hurt( DamageSource.MAGIC, 1.0f );
		stealer.heal( 1.0f );
	}
}
