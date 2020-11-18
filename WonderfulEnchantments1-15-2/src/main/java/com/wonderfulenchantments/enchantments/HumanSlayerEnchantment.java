package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends DamageEnchantment {
	public HumanSlayerEnchantment() {
		super( Rarity.UNCOMMON, 3, EquipmentSlotType.MAINHAND );
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 5 + ( enchantmentLevel - 1 ) * 8 + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public float calcDamageByCreature( int level, CreatureAttribute creatureType ) {
		return 0.0f;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		Entity entitySource = event.getSource().getImmediateSource();

		if( entitySource instanceof LivingEntity ) {
			LivingEntity target = event.getEntityLiving(), attacker = ( LivingEntity )entitySource;
			float extraDamage = ( float )Math.floor( 2.5F * EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.HUMAN_SLAYER.get(), attacker ) );

			if( extraDamage > 0.0F && isHuman( target ) ) {
				( ( ServerWorld )attacker.getEntityWorld() ).spawnParticle( ParticleTypes.ENCHANTED_HIT, target.getPosX(), target.getPosYHeight( 0.625D ), target.getPosZ(), 24, 0.125D, 0.25D, 0.125D, 0.5D );
				event.setAmount( event.getAmount() + extraDamage );
			}
		}
	}

	protected static boolean isHuman( Entity entity ) {
		return ( entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity || entity instanceof PlayerEntity || entity instanceof WitchEntity || entity instanceof AbstractIllagerEntity );
	}
}
