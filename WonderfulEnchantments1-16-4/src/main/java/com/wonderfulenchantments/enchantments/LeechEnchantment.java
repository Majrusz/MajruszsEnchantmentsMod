package com.wonderfulenchantments.enchantments;

import com.mlib.effects.EffectHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.ConfigHandlerOld.Config;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
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

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that steals from enemy positive effect or some health. */
@Mod.EventBusSubscriber
public class LeechEnchantment extends Enchantment {
	public LeechEnchantment() {
		super( Rarity.UNCOMMON, EnchantmentType.WEAPON, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return enchantmentLevel * 20 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canApply( stack );
	}

	/** Event that applies enchantment effect when attacker has an appropriate enchantment level and if 'leeching' succeeded. */
	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		if( !WonderfulEnchantmentHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		if( WonderfulEnchantments.RANDOM.nextDouble() >= Config.LEECH_CHANCE.get() )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource()
			.getImmediateSource();
		LivingEntity target = event.getEntityLiving();

		int vampirismLevel = 0;
		if( attacker != null )
			vampirismLevel = EnchantmentHelperPlus.calculateEnchantmentSum( RegistryHandler.VAMPIRISM.get(), attacker.getArmorInventoryList() );

		if( EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.LEECH.get(), attacker ) > 0 ) {
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
				EffectHelper.applyEffectIfPossible( stealer, effect );
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
