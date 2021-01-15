package com.wonderfulenchantments.enchantments;

import com.mlib.TimeConverter;
import com.mlib.effects.EffectHelper;
import com.wonderfulenchantments.ConfigHandlerOld.Config;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that inflicts on the enemy several negative effects. */
@Mod.EventBusSubscriber
public class PufferfishVengeanceEnchantment extends Enchantment {
	public PufferfishVengeanceEnchantment() {
		super( Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 5 + enchantmentLevel * 12 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canApply( stack );
	}

	/** Event that checks if attacker has an appropriate enchantment level. */
	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		DamageSource damageSource = event.getSource();

		if( !( damageSource.getImmediateSource() instanceof LivingEntity ) )
			return;

		LivingEntity attacker = ( LivingEntity )damageSource.getImmediateSource();
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.PUFFERFISH_VENGEANCE.get(), attacker );

		if( enchantmentLevel <= 0 )
			return;

		LivingEntity target = event.getEntityLiving();
		int durationInTicks = TimeConverter.secondsToTicks( 2 * Config.PUFFERFISH_DURATION.get() + 1 );
		World world = attacker.getEntityWorld();

		applyEffects( target, durationInTicks );
		world.playSound( null, target.getPosX(), target.getPosY(), target.getPosZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.AMBIENT,
			1.0F, 1.0F
		);
	}

	/**
	 Applying negative effects on given target.

	 @param target          Entity on which negative effects will be applied.
	 @param durationInTicks Duration how long effect will last. (in ticks)
	 */
	protected static void applyEffects( LivingEntity target, int durationInTicks ) {
		EffectHelper.applyEffectIfPossible( target, Effects.HUNGER, durationInTicks, 2 );
		EffectHelper.applyEffectIfPossible( target, Effects.POISON, durationInTicks, 3 );
		EffectHelper.applyEffectIfPossible( target, Effects.NAUSEA, durationInTicks, 0 );
	}
}
