package com.wonderfulenchantments.enchantments;

import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
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
		super( "hunter", Rarity.RARE, RegistryHandler.BOW_AND_CROSSBOW, EquipmentSlotType.MAINHAND, "Hunter" );
		String damage_comment = "Extra damage multiplier to distance per enchantment level.";
		String distance_comment = "Minimum required distance to not get any damage penalty.";
		String penalty_comment = "Maximum damage penalty if entity is very close.";
		this.damageMultiplier = new DoubleConfig( "damage_multiplier", damage_comment, false, 0.0001, 0.0, 0.01 );
		this.minimumDistance = new DoubleConfig( "minimum_distance", distance_comment, false, 80.0, 1.0, 10000.0 );
		this.damagePenaltyMultiplier = new DoubleConfig( "penalty_multiplier", penalty_comment, false, 0.5, 0.0, 1.0 );
		this.enchantmentGroup.addConfigs( this.damageMultiplier, this.minimumDistance, this.damagePenaltyMultiplier );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 50 );
		setMinimumEnchantabilityCalculator( level->( 15 + ( level - 1 ) * 9 ) );
	}

	/** Event at which loot will be increased when killer killed entity with bow and have this enchantment. */
	@SubscribeEvent
	public static void spawnExtraLoot( LootingLevelEvent event ) {
		DamageSource damageSource = event.getDamageSource();

		if( !isValid( damageSource ) )
			return;

		LivingEntity entity = ( LivingEntity )damageSource.getTrueSource();
		int hunterLevel = EnchantmentHelper.getEnchantmentLevel( Instances.HUNTER, entity.getHeldItemMainhand() );
		event.setLootingLevel( event.getLootingLevel() + hunterLevel );
	}

	/** Event that increases damage dealt by entity. */
	@SubscribeEvent
	public static void onHit( LivingHurtEvent event ) {
		DamageSource damageSource = event.getSource();
		LivingEntity target = event.getEntityLiving();

		if( !isValid( damageSource ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getTrueSource();
		HunterEnchantment enchantment = Instances.HUNTER;
		int hunterLevel = EnchantmentHelper.getEnchantmentLevel( enchantment, attacker.getHeldItemMainhand() );
		if( hunterLevel <= 0 )
			return;

		double distance = attacker.getPositionVec()
			.squareDistanceTo( target.getPositionVec() );
		double penaltyMultiplier = Math.max( 1.0 - distance / enchantment.minimumDistance.get(), 0.0 ) * enchantment.damagePenaltyMultiplier.get();
		double extraDamageMultiplier = distance * enchantment.damageMultiplier.get() * hunterLevel + 1.0 - penaltyMultiplier;
		event.setAmount( ( float )( event.getAmount() * extraDamageMultiplier ) );
	}

	/**
	 Checking if damage source comes from arrow and is caused (fired) by the entity. (not dispenser for example)

	 @param source Damage source to check.
	 */
	protected static boolean isValid( DamageSource source ) {
		return source != null && source.getImmediateSource() instanceof ArrowEntity && source.getTrueSource() instanceof LivingEntity;
	}
}
