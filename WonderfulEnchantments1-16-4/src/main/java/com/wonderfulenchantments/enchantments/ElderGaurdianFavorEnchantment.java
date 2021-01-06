package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class ElderGaurdianFavorEnchantment extends Enchantment {
	protected static final String linkTag = "ElderGuardianFavorLinkedEntityID";

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
		if( !WonderfulEnchantmentHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource().getTrueSource();
		LivingEntity target = event.getEntityLiving();

		connectEntities( attacker, target );
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity attacker = event.getEntityLiving();
		CompoundNBT data = attacker.getPersistentData();

		if( !data.hasUniqueId( linkTag ) )
			return;

		int targetID = data.getInt( linkTag );
		World world = attacker.world;

		if( !( world.getEntityByID( targetID ) instanceof LivingEntity ) )
			return;

		LivingEntity target = ( LivingEntity )world.getEntityByID( targetID );

		if( world instanceof ClientWorld )
			spawnParticles( attacker, target, ( ClientWorld )world );
	}

	protected static void connectEntities( LivingEntity attacker, LivingEntity target ) {
		WonderfulEnchantments.LOGGER.info( attacker + "/" + target );

		attacker.getPersistentData().putInt( linkTag, target.getEntityId() );
	}

	protected static void spawnParticles( LivingEntity attacker, LivingEntity target, ClientWorld world ) {
		double d5 = (double)0.5;
		double d0 = target.getPosX() - attacker.getPosX();
		double d1 = target.getPosYHeight(0.5D) - attacker.getPosYEye();
		double d2 = target.getPosZ() - attacker.getPosZ();
		double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
		d0 = d0 / d3;
		d1 = d1 / d3;
		d2 = d2 / d3;
		double d4 = WonderfulEnchantments.RANDOM.nextDouble();

		WonderfulEnchantments.LOGGER.info( "Particles: " + d4 + "/" + d3 );
		while(d4 < d3) {
			d4 += 1.8D - d5 + WonderfulEnchantments.RANDOM.nextDouble() * (1.7D - d5);
			world.addParticle( ParticleTypes.CRIT, target.getPosX() + d0 * d4, target.getPosYEye() + d1 * d4, target.getPosZ() + d2 * d4, 0.0D, 0.0D, 0.0D);
		}
	}
}
