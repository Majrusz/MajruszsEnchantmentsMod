package com.wonderfulenchantments.enchantments;

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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class LeechEnchantment extends Enchantment {
	protected static final double leechChance = 0.25D;

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

	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		if( !WonderfulEnchantmentHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		if( WonderfulEnchantments.RANDOM.nextDouble() >= leechChance )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource().getImmediateSource();
		LivingEntity target = event.getEntityLiving();

		if( EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.LEECH.get(), attacker ) > 0 ) {
			steal( attacker, target );
			spawnParticlesAndPlaySounds( attacker, target );
		}
	}

	protected static void steal( LivingEntity stealer, LivingEntity target ) {
		Collection< EffectInstance > targetEffects = target.getActivePotionEffects();

		// if there is no effects at all or there is no positive effect then steal health
		if( !( targetEffects.size() > 0 && stealEffect( stealer, target, targetEffects ) ) )
			stealHealth( stealer, target );
	}

	protected static boolean stealEffect( LivingEntity stealer, LivingEntity target, Collection< EffectInstance > effects ) {
		EffectInstance[] possibleEffects = effects.toArray( new EffectInstance[]{} );

		for( EffectInstance effect : possibleEffects )
			if( effect.getPotion().isBeneficial() ) {
				stealer.addPotionEffect( effect );
				target.removePotionEffect( effect.getPotion() );

				return true;
			}

		return false;
	}

	protected static void stealHealth( LivingEntity stealer, LivingEntity target ) {
		target.attackEntityFrom( DamageSource.MAGIC, 1.0f );
		stealer.heal( 1.0f );
	}

	protected static void spawnParticlesAndPlaySounds( LivingEntity attacker, LivingEntity target ) {
		if( attacker.getEntityWorld() instanceof ServerWorld ) {
			ServerWorld world = ( ServerWorld )attacker.getEntityWorld();

			Vec3d startPosition = attacker.getPositionVec().add( new Vec3d( 0.0D, attacker.getHeight() * 0.75D, 0.0D ) );
			Vec3d endPosition = target.getPositionVec().add( new Vec3d( 0.0D, target.getHeight() * 0.75D, 0.0D ) );

			Vec3d difference = endPosition.subtract( startPosition );
			int amountOfParticles = ( int )( Math.ceil( startPosition.distanceTo( endPosition ) * 5.0D ) );

			for( int i = 0; i <= amountOfParticles; i++ ) {
				Vec3d step = difference.scale( ( float )( i ) / amountOfParticles );
				Vec3d finalPosition = startPosition.add( step );
				world.spawnParticle( ParticleTypes.ENCHANTED_HIT, finalPosition.getX(), finalPosition.getY(), finalPosition.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D );
			}

			world.playSound( null, startPosition.getX(), startPosition.getY(), startPosition.getZ(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.AMBIENT, 0.25F, 1.0F );
		}
	}
}
