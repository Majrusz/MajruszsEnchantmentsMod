package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.damage.DamageHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases damage dealt against humans. (pillagers, villagers, players and witches) */
@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig damageBonus;

	public HumanSlayerEnchantment() {
		super( Rarity.UNCOMMON, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, "AgainstHumanity" );
		String comment = "Damage bonus per enchantment level.";
		this.damageBonus = new DoubleConfig( "damage_bonus", comment, false, 2.5, 1.0, 10.0 );
		this.enchantmentGroup.addConfig( this.damageBonus );

		setMaximumEnchantmentLevel( 5 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 5 + ( level - 1 ) * 8 ) );
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canApply( stack );
	}

	/** Event that increases damage when all conditions are met. */
	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		if( !DamageHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource()
			.getImmediateSource();
		LivingEntity target = event.getEntityLiving();
		HumanSlayerEnchantment enchantment = Instances.HUMAN_SLAYER;
		float extraDamage = ( float )Math.floor( enchantment.damageBonus.get() * EnchantmentHelper.getMaxEnchantmentLevel( enchantment, attacker ) );

		if( extraDamage > 0.0f && isHuman( target ) ) {
			( ( ServerWorld )attacker.getEntityWorld() ).spawnParticle( ParticleTypes.ENCHANTED_HIT, target.getPosX(), target.getPosYHeight( 0.625 ),
				target.getPosZ(), 24, 0.125, 0.25, 0.125, 0.5
			);
			event.setAmount( event.getAmount() + extraDamage );
		}
	}

	/**
	 Checking if entity is human.

	 @param entity Entity to check.
	 */
	protected static boolean isHuman( Entity entity ) {
		return ( entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity || entity instanceof PlayerEntity || entity instanceof WitchEntity || entity instanceof AbstractIllagerEntity );
	}
}
