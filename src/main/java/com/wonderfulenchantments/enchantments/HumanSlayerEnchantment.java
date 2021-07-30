package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.damage.DamageHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases damage dealt against humans. (pillagers, villagers, players and witches) */
@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig damageBonus;

	public HumanSlayerEnchantment() {
		super( "human_slayer", Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, "AgainstHumanity" );
		String comment = "Damage bonus per enchantment level.";
		this.damageBonus = new DoubleConfig( "damage_bonus", comment, false, 2.5, 1.0, 10.0 );
		this.enchantmentGroup.addConfig( this.damageBonus );

		setMaximumEnchantmentLevel( 5 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 5 + ( level - 1 ) * 8 ) );
	}

	@Override
	public boolean canEnchant( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canEnchant( stack );
	}

	@Override
	protected boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DamageEnchantment ) && super.checkCompatibility( enchantment );
	}

	/** Event that increases damage when all conditions are met. */
	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		if( !DamageHelper.isDirectDamageFromLivingEntity( event.getSource() ) )
			return;

		LivingEntity attacker = ( LivingEntity )event.getSource()
			.getDirectEntity();
		LivingEntity target = event.getEntityLiving();
		HumanSlayerEnchantment enchantment = Instances.HUMAN_SLAYER;
		float extraDamage = ( float )Math.floor( enchantment.damageBonus.get() * EnchantmentHelper.getEnchantmentLevel( enchantment, attacker ) );

		if( extraDamage > 0.0f && isHuman( target ) ) {
			( ( ServerLevel )attacker.level ).sendParticles( ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY( 0.625 ),
				target.getZ(), 24, 0.125, 0.25, 0.125, 0.5
			);
			event.setAmount( event.getAmount() + extraDamage );
		}
	}

	/**
	 Checking if entity is human.

	 @param entity Entity to check.
	 */
	protected static boolean isHuman( Entity entity ) {
		return ( entity instanceof Villager || entity instanceof WanderingTrader || entity instanceof Player || entity instanceof Witch || entity instanceof Pillager );
	}
}
