package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.MajruszLibrary;
import com.mlib.config.AvailabilityConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that increases experience from all sources. */
@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig experienceMultiplier;
	protected final AvailabilityConfig extraLevels;
	protected final DoubleConfig enchantmentLevelsMultiplier;
	protected final IntegerConfig levelCap;

	public EnlightenmentEnchantment() {
		super( "enlightenment", Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "Enlightenment" );
		String experienceComment = "Increases experience from all sources by that multiplier per enchantment level.";
		String levelsComment = "Increases experience from all sources by that multiplier per enchantment level. (enchantment table may not work properly!)";
		String enchantmentComment = "Enchantment levels increase by that multiplier per enchantment level.";
		String capComment = "Enchantment levels limit at enchanting table.";
		this.experienceMultiplier = new DoubleConfig( "experience_multiplier", experienceComment, false, 0.25, 0.01, 10.0 );
		this.extraLevels = new AvailabilityConfig( "extra_levels", levelsComment, false, false );
		this.enchantmentLevelsMultiplier = new DoubleConfig( "enchantment_levels_multiplier", enchantmentComment, false, 0.125, 0.01, 0.5 );
		this.levelCap = new IntegerConfig( "level_cap", capComment, false, 30, 30, 60 );
		this.enchantmentGroup.addConfigs( this.experienceMultiplier, this.extraLevels, this.enchantmentLevelsMultiplier, this.levelCap );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 6 + level * 12 ) );
	}

	/** Event that increases experience when picking up experience orbs. */
	@SubscribeEvent
	public static void onXPPickUp( PlayerXpEvent.PickupXp event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		Player player = event.getPlayer();

		int enlightenmentSum = EnchantmentHelperPlus.calculateEnchantmentSum( enlightenment, player, EquipmentSlots.ARMOR );
		if( enlightenmentSum > 0 ) {
			double bonusRatio = enlightenment.experienceMultiplier.get() * enlightenmentSum;
			double randomBonus = bonusRatio * MajruszLibrary.RANDOM.nextDouble();
			int bonusExperience = ( int )( Math.round( randomBonus * ( double )event.getOrb()
				.value )
			);
			player.giveExperiencePoints( bonusExperience );
		}
	}

	/** Event that increases levels at the enchantment table with each enchantment level. (gives 0%-100% level bonus, capped at 30lvl) */
	@SubscribeEvent
	public static void onCalculatingEnchantmentLevels( EnchantmentLevelSetEvent event ) {
		EnlightenmentEnchantment enlightenment = Instances.ENLIGHTENMENT;
		if( enlightenment.areExtraLevelsDisabled() )
			return;

		ServerLevel world = ( ServerLevel )event.getWorld();
		BlockPos position = event.getPos();
		Player player = world.getNearestPlayer( TargetingConditions.DEFAULT, position.getX(), position.getY(), position.getZ() );

		if( player == null )
			return;

		int enlightenmentSum = EnchantmentHelperPlus.calculateEnchantmentSum( enlightenment, player, EquipmentSlots.ARMOR );
		if( enlightenmentSum > 0 ) {
			int bonus = ( int )Math.max( 0, Math.min( event.getLevel() * enlightenmentSum * enlightenment.enchantmentLevelsMultiplier.get(),
				enlightenment.levelCap.get() - event.getLevel()
			) );

			event.setLevel( event.getLevel() + bonus );
		}
	}

	/** Returns whether calculating extra levels on enchanting table should work. */
	private boolean areExtraLevelsDisabled() {
		return this.extraLevels.isDisabled();
	}
}