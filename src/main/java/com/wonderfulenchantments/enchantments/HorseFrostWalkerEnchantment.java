package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.EquipmentSlots;
import com.mlib.LevelHelper;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that gives Frost Walker enchantment effect but on horse armor. */
@Mod.EventBusSubscriber
public class HorseFrostWalkerEnchantment extends WonderfulEnchantment {
	public HorseFrostWalkerEnchantment() {
		super( "horse_frost_walker", Rarity.RARE, RegistryHandler.HORSE_ARMOR, EquipmentSlots.ARMOR, "HorseFrostWalker" );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 15 );
		setMinimumEnchantabilityCalculator( level->( 10 * level ) );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	/** Event that freezes nearby water each tick when all conditions are met. */
	@SubscribeEvent
	public static void freezeNearby( LivingEvent.LivingUpdateEvent event ) {
		Animal animal = CommonHelper.castIfPossible( Animal.class, event.getEntityLiving() );
		if( animal == null || animal.level.isClientSide() )
			return;

		int frostLevel = Instances.HORSE_FROST_WALKER.getEnchantmentSum( animal.getArmorSlots() );
		LevelHelper.freezeWater( animal, 2 + frostLevel, 60, 120 );
	}

	/** Disabling taking damage when horse is standing on Magma Block. */
	@SubscribeEvent
	public static void onTakingDamage( LivingDamageEvent event ) {
		if( event.getSource() != DamageSource.HOT_FLOOR )
			return;

		Animal animal = CommonHelper.castIfPossible( Animal.class, event.getEntityLiving() );
		if( animal != null && Instances.HORSE_FROST_WALKER.getEnchantmentSum( animal.getArmorSlots() ) > 0 )
			event.setCanceled( true );
	}
}
