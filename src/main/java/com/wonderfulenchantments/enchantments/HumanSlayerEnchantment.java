package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.damage.DamageHelper;
import com.mlib.entities.EntityHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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

	/** Event that increases damage when all conditions were met. */
	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		if( !DamageHelper.areEntitiesInstancesOf( damageSource, LivingEntity.class, LivingEntity.class ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getDirectEntity();
		LivingEntity target = event.getEntityLiving();
		HumanSlayerEnchantment humanSlayer = Instances.HUMAN_SLAYER;
		float extraDamage = ( float )Math.floor( humanSlayer.damageBonus.get() * humanSlayer.getEnchantmentLevel( attacker ) );

		if( extraDamage > 0.0f && EntityHelper.isHuman( target ) ) {
			( ( ServerLevel )attacker.level ).sendParticles( ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY( 0.625 ), target.getZ(), 24,
				0.125, 0.25, 0.125, 0.5
			);
			event.setAmount( event.getAmount() + extraDamage );
		}
	}
}
