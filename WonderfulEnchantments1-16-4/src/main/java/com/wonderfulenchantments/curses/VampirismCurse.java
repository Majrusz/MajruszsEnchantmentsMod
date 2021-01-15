package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import com.mlib.TimeConverter;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Weakens entity when it is outside during the day. */
@Mod.EventBusSubscriber
public class VampirismCurse extends WonderfulCurse {
	protected static Effect[] effects = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.HUNGER };
	protected static int counter = 0;
	protected static int updateDelay = TimeConverter.secondsToTicks( 5.0 );
	protected final DurationConfig effectDuration;

	public VampirismCurse() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "Vampirism" );
		String comment = "Duration of negative effects. (in seconds)";
		this.effectDuration = this.curseGroup.addConfig( new DurationConfig( "effect_duration", comment, false, 30.0, 10.0, 300.0 ) );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@SubscribeEvent
	public static void onUpdate( TickEvent.PlayerTickEvent event ) {
		if( !( event.player.world instanceof ServerWorld ) )
			return;

		counter = ( counter + 1 ) % updateDelay;
		if( counter != 0 )
			return;
		
		PlayerEntity player = event.player;
		ServerWorld world = ( ServerWorld )event.player.world;
		VampirismCurse vampirism = Instances.VAMPIRISM;
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( vampirism, player.getArmorInventoryList() );

		if( enchantmentLevel == 0 || !isPlayerOutsideDuringTheDay( player, world ) )
			return;

		for( Effect effect : effects )
			EffectHelper.applyEffectIfPossible( player, effect, vampirism.effectDuration.getDuration() * enchantmentLevel, 0 );

		player.setFire( 3 + 2 * enchantmentLevel );
	}

	/** Checks whether player is outside during the day. */
	protected static boolean isPlayerOutsideDuringTheDay( PlayerEntity player, ServerWorld world ) {
		return world.canSeeSky( new BlockPos( player.getPositionVec() ) ) && world.isDaytime();
	}
}
