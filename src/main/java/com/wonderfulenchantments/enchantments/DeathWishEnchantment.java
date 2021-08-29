package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.mlib.damage.DamageHelper;
import com.mlib.entities.EntityHelper;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases a damage equal to the percentage of their health lost. */
@Mod.EventBusSubscriber
public class DeathWishEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig maximumDamageMultiplier;
	protected final DoubleConfig vulnerabilityMultiplier;

	public DeathWishEnchantment() {
		super( "death_wish", Rarity.RARE, RegistryHandler.MELEE_WEAPON, EquipmentSlot.MAINHAND, "DeathWish" );

		String damageComment = "Maximum damage bonus player can get from this enchantment. (at full health - 0% * value, at half health - 50% * value, at 0 health - 100% * value)";
		this.maximumDamageMultiplier = new DoubleConfig( "maximum_damage_multiplier", damageComment, false, 1.0, 0.01, 10.0 );

		String vulnerabilityComment = "Whenever the owner takes damage, the damage is multiplied by this value.";
		this.vulnerabilityMultiplier = new DoubleConfig( "vulnerability_multiplier", vulnerabilityComment, false, 1.25, 1.0, 10.0 );

		this.enchantmentGroup.addConfigs( this.maximumDamageMultiplier, this.vulnerabilityMultiplier );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->12 );
	}

	/** Event that updates the movement speed bonus on each animal entity equipment change. */
	@SubscribeEvent
	public static void onDamageDealt( LivingHurtEvent event ) {
		DeathWishEnchantment deathWish = Instances.DEATH_WISH;

		LivingEntity attacker = DamageHelper.getEntityFromDamageSource( LivingEntity.class, event.getSource() );
		if( attacker != null && deathWish.hasEnchantment( attacker ) )
			event.setAmount( event.getAmount() * deathWish.getDamageMultiplier( attacker ) );

		LivingEntity target = event.getEntityLiving();
		if( deathWish.hasEnchantment( target ) )
			event.setAmount( event.getAmount() * deathWish.getVulnerabilityMultiplier() );
	}

	/** Returns damage multiplier depending on missing health and config. */
	private float getDamageMultiplier( LivingEntity entity ) {
		return ( float )( 1.0f + EntityHelper.getMissingHealthRatio( entity ) * this.maximumDamageMultiplier.get() );
	}

	/** Returns vulnerability damage multiplier. */
	private float getVulnerabilityMultiplier() {
		return this.vulnerabilityMultiplier.get()
			.floatValue();
	}
}
