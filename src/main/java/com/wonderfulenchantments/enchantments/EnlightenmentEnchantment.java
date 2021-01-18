package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.MajruszLibrary;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases experience from all sources. */
@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig experienceMultiplier;
	protected final DoubleConfig enchantmentLevelsMultiplier;
	protected final IntegerConfig levelCap;

	public EnlightenmentEnchantment() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "Enlightenment" );
		String experience_comment = "Increases experience from all sources by that multiplier per enchantment level.";
		String enchantment_comment = "Enchantment levels increase by that multiplier per enchantment level.";
		String cap_comment = "Enchantment levels limit at enchanting table.";
		this.experienceMultiplier = new DoubleConfig( "experience_multiplier", experience_comment, false, 0.25, 0.01, 10.0 );
		this.enchantmentLevelsMultiplier = new DoubleConfig( "enchantment_levels_multiplier", enchantment_comment, false, 0.125, 0.01, 0.5 );
		this.levelCap = new IntegerConfig( "level_cap", cap_comment, false, 30, 30, 60 );
		this.enchantmentGroup.addConfigs( this.experienceMultiplier, this.enchantmentLevelsMultiplier, this.levelCap );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 6 + level * 12 ) );
	}

	/** Event that increases experience when picking up experience orbs. */
	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		PlayerEntity player = event.getPlayer();

		int enlightenmentSum = EnchantmentHelperPlus.calculateEnchantmentSum( enlightenment, player, EquipmentSlotTypes.ARMOR );
		if( enlightenmentSum > 0 ) {
			double bonusRatio = enlightenment.experienceMultiplier.get() * enlightenmentSum;
			double randomBonus = bonusRatio * MajruszLibrary.RANDOM.nextDouble();
			int bonusExperience = ( int )( Math.round( randomBonus * ( double )event.getOrb()
				.getXpValue() )
			);
			player.giveExperiencePoints( bonusExperience );
		}
	}

	/** Event that increases levels at the enchantment table with each enchantment level. (gives 0%-100% level bonus, capped at 30lvl) */
	@SubscribeEvent
	public static void onCalculatingEnchantmentLevels( EnchantmentLevelSetEvent event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		BlockPos position = event.getPos();
		PlayerEntity player = event.getWorld()
			.getClosestPlayer( EntityPredicate.DEFAULT, position.getX(), position.getY(), position.getZ() );

		int enlightenmentSum = EnchantmentHelperPlus.calculateEnchantmentSum( enlightenment, player, EquipmentSlotTypes.ARMOR );

		if( enlightenmentSum > 0 ) {
			int bonus = ( int )Math.max( 0, Math.min( event.getLevel() * enlightenmentSum * enlightenment.enchantmentLevelsMultiplier.get(),
				enlightenment.levelCap.get() - event.getLevel()
			) );

			event.setLevel( event.getLevel() + bonus );
		}
	}
}