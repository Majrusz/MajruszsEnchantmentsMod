package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.LevelHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
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
		if( !isValid( event ) )
			return;

		Animal animal = ( Animal )event.getEntityLiving();
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.HORSE_FROST_WALKER, animal.getArmorSlots() );
		if( enchantmentLevel > 0 )
			LevelHelper.freezeWater( animal, 2 + enchantmentLevel, 60, 120 );
	}

	/** Disabling taking damage when horse is standing on Magma Block. */
	@SubscribeEvent
	public static void onTakingDamage( LivingDamageEvent event ) {
		if( !( event.getEntityLiving() instanceof Animal ) || !( event.getSource() == DamageSource.HOT_FLOOR ) )
			return;

		Animal animal = ( Animal )event.getEntityLiving();
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( Instances.HORSE_FROST_WALKER, animal.getArmorSlots() );
		if( enchantmentLevel > 0 )
			event.setCanceled( true );
	}

	/**
	 Checking whether all conditions are met.

	 @param event Living entity update event.
	 */
	protected static boolean isValid( LivingEvent.LivingUpdateEvent event ) {
		if( !( event.getEntityLiving() instanceof Animal ) )
			return false;

		Animal animal = ( Animal )event.getEntityLiving();
		if( animal.level.isClientSide() || !animal.isOnGround() )
			return false;

		for( ItemStack itemStack : animal.getArmorSlots() )
			if( itemStack.getItem() instanceof HorseArmorItem )
				return true;

		return false;
	}
}
