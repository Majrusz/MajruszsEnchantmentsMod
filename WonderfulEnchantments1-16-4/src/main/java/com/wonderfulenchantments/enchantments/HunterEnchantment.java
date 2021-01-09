package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that increases loot from enemies and increases damage the further the enemy is. */
@Mod.EventBusSubscriber
public class HunterEnchantment extends LootBonusEnchantment {
	public HunterEnchantment() {
		super( Rarity.RARE, EnchantmentType.BOW, EquipmentSlotType.MAINHAND );
	}

	@Override
	public int getMinEnchantability( int level ) {
		return super.getMinEnchantability( level ) + increaseLevelIfEnchantmentIsDisabled( this );
	}

	/** Event at which loot will be increased when killer killed entity with bow and have this enchantment. */
	@SubscribeEvent
	public static void spawnExtraLoot( LootingLevelEvent event ) {
		DamageSource damageSource = event.getDamageSource();

		if( !isValid( damageSource ) )
			return;

		LivingEntity entity = ( LivingEntity )damageSource.getTrueSource();
		int hunterLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.HUNTER.get(), entity.getHeldItemMainhand() );
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
		int hunterLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.HUNTER.get(), attacker.getHeldItemMainhand() );
		double extraDamageMultiplier = ( attacker.getPositionVec()
			.squareDistanceTo( target.getPositionVec() )
		) * ConfigHandler.Config.HUNTER_MULTIPLIER.get() * hunterLevel + 1.0;

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
