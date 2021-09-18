package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.math.VectorHelper;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases loot from enemies and increases damage the further the enemy is. */
@Mod.EventBusSubscriber
public class HunterEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig damageMultiplier;
	protected final DoubleConfig minimumDistance;
	protected final DoubleConfig damagePenaltyMultiplier;

	public HunterEnchantment() {
		super( "hunter", Rarity.RARE, RegistryHandler.BOW_AND_CROSSBOW, EquipmentSlot.MAINHAND, "Hunter" );

		String damageComment = "Extra damage multiplier to distance per enchantment level.";
		this.damageMultiplier = new DoubleConfig( "damage_multiplier", damageComment, false, 0.0001, 0.0, 0.01 );

		String distanceComment = "Minimum required distance to not get any damage penalty.";
		this.minimumDistance = new DoubleConfig( "minimum_distance", distanceComment, false, 80.0, 1.0, 10000.0 );

		String penaltyComment = "Maximum damage penalty if a mob is very close.";
		this.damagePenaltyMultiplier = new DoubleConfig( "penalty_multiplier", penaltyComment, false, 0.5, 0.0, 1.0 );

		this.enchantmentGroup.addConfigs( this.damageMultiplier, this.minimumDistance, this.damagePenaltyMultiplier );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 50 );
		setMinimumEnchantabilityCalculator( level->( 15 + ( level - 1 ) * 9 ) );
	}

	/** Event at which loot will be increased when killer killed entity with bow and have this enchantment. */
	@SubscribeEvent
	public static void spawnExtraLoot( LootingLevelEvent event ) {
		DamageSource damageSource = event.getDamageSource();
		if( damageSource == null )
			return;

		LivingEntity entity = CommonHelper.castIfPossible( LivingEntity.class, damageSource.getEntity() );
		if( isValid( damageSource ) && entity != null )
			event.setLootingLevel( event.getLootingLevel() + Instances.HUNTER.getEnchantmentLevel( entity.getMainHandItem() ) );
	}

	/** Event that increases damage dealt by entity. */
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		LivingEntity target = event.getEntityLiving();
		LivingEntity attacker = CommonHelper.castIfPossible( LivingEntity.class, damageSource.getEntity() );
		HunterEnchantment hunter = Instances.HUNTER;

		if( isValid( damageSource ) && attacker != null && hunter.hasEnchantment( attacker.getMainHandItem() ) )
			event.setAmount( ( float )( event.getAmount() * hunter.getDamageMultiplier( attacker, target ) ) );
	}

	/** Checks if a damage source comes from an arrow and is caused (fired) by the entity. (not dispenser for example) */
	protected static boolean isValid( DamageSource source ) {
		return source != null && source.getDirectEntity() instanceof Arrow && source.getEntity() instanceof LivingEntity;
	}

	/** Returns the final damage multiplier. */
	protected double getDamageMultiplier( LivingEntity attacker, LivingEntity target ) {
		int hunterLevel = getEnchantmentLevel( attacker.getMainHandItem() );
		double distance = VectorHelper.distanceSquared( attacker.position(), target.position() );
		double penaltyMultiplier = Math.max( 1.0 - distance / this.minimumDistance.get(), 0.0 ) * this.damagePenaltyMultiplier.get();

		return distance * this.damageMultiplier.get() * hunterLevel + 1.0 - penaltyMultiplier;
	}
}
