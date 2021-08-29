package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.EquipmentSlots;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases experience from all sources. */
@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig experienceMultiplier;

	public EnlightenmentEnchantment() {
		super( "enlightenment", Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "Enlightenment" );

		String experienceComment = "Increases experience from all sources by that multiplier per enchantment level.";
		this.experienceMultiplier = new DoubleConfig( "experience_multiplier", experienceComment, false, 0.25, 0.01, 10.0 );

		this.enchantmentGroup.addConfigs( this.experienceMultiplier );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 6 + level * 12 ) );
	}

	/** Event that increases experience when picking up experience orbs. */
	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		Player player = event.getPlayer();

		int enlightenmentSum = enlightenment.getEnchantmentSum( player, EquipmentSlots.ARMOR );
		if( enlightenmentSum > 0 )
			player.giveExperiencePoints( enlightenment.getRandomizedExperience( enlightenmentSum, event.getOrb() ) );
	}

	@SubscribeEvent
	public static void onEquipmentChanged( LivingEquipmentChangeEvent event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		ServerPlayer player = CommonHelper.castIfPossible( ServerPlayer.class, event.getEntityLiving() );
		if( player != null && enlightenment.getEnchantmentSum( player, EquipmentSlots.ARMOR ) == 8 )
			Instances.BASIC_TRIGGER.trigger( player, "enlightenment_8" );
	}

	/** Returns randomized amount of experience. */
	protected int getRandomizedExperience( int enlightenmentSum, ExperienceOrb experienceOrb ) {
		double maximumAmountOfExperiencePoints = this.experienceMultiplier.get() * enlightenmentSum * experienceOrb.getValue();

		return Random.randomizeExperience( maximumAmountOfExperiencePoints * MajruszLibrary.RANDOM.nextDouble() );
	}
}