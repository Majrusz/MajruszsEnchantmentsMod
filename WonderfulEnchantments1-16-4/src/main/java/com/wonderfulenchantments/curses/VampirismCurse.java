package com.wonderfulenchantments.curses;

import com.wonderfulenchantments.ConfigHandler.Config;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class VampirismCurse extends Enchantment {
	protected static Effect[] effects = new Effect[] {
		Effects.WEAKNESS, Effects.SLOWNESS, Effects.HUNGER
	};
	protected static int counter = 0;
	protected static int updateDelay = WonderfulEnchantmentHelper.secondsToTicks( 5.0 );

	public VampirismCurse() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 10 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 40;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return super.canApplyTogether( enchantment );
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return true;
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
		int enchantmentLevel = WonderfulEnchantmentHelper.calculateEnchantmentSum( RegistryHandler.VAMPIRISM.get(), player.getArmorInventoryList() );

		if( enchantmentLevel == 0 || !isPlayerOutsideInDay( player, world ) )
			return;

		int effectDurationInTicks = WonderfulEnchantmentHelper.secondsToTicks( Config.VAMPIRISM_DURATION.get() ) * enchantmentLevel;
		for( Effect effect : effects )
			WonderfulEnchantmentHelper.applyEffectIfPossible( player, effect, effectDurationInTicks, 0 );

		player.setFire( 3 + 2 * enchantmentLevel );
	}

	protected static boolean isPlayerOutsideInDay( PlayerEntity player, ServerWorld world ) {
		return world.canSeeSky( new BlockPos( player.getPositionVec() ) ) && world.isDaytime();
	}
}
