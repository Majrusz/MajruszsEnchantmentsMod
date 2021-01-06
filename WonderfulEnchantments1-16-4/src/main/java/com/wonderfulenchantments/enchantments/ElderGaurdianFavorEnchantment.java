package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;
import java.util.function.Predicate;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class ElderGaurdianFavorEnchantment extends Enchantment {
	protected static final String linkTag = "ElderGuardianFavorLinkedEntityID";
	protected static final String linkCounterTag = "ElderGuardianFavorCounter";
	protected static final int damageCooldown = 70;

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

			target.attackEntityFrom( DamageSource.causeMobDamage( attacker ), ( areEntitiesInWater ? 2.0f : 1.0f ) * 10.0f );
		}
	}

	protected static void connectEntities( LivingEntity attacker, LivingEntity target, int enchantmentLevel ) {
		CompoundNBT data = attacker.getPersistentData();

		if( data.getInt( linkCounterTag ) > 0 || enchantmentLevel == 0 )
			return;

		data.putInt( linkTag, target.getEntityId() );
		data.putInt( linkCounterTag, damageCooldown );
	}

	protected static void spawnParticles( LivingEntity attacker, LivingEntity target, ServerWorld world ) {
		Vector3d difference = new Vector3d(
			attacker.getPosX() - target.getPosX(),
			attacker.getPosYHeight( 0.5 ) - target.getPosYHeight( 0.5 ),
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

			factor += 1.8 - 0.8 + WonderfulEnchantments.RANDOM.nextDouble() * (1.7 - 0.8);
		}
	}
}
