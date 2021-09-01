package com.wonderfulenchantments.enchantments;

import com.mlib.CommonHelper;
import com.mlib.TimeConverter;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that inflicts on the enemy several negative effects. */
@Mod.EventBusSubscriber
public class PufferfishVengeanceEnchantment extends WonderfulEnchantment {
	protected final DurationConfig durationConfig;
	protected final IntegerConfig hungerAmplifier, poisonAmplifier, nauseaAmplifier;

	public PufferfishVengeanceEnchantment() {
		super( "pufferfish_vengeance", Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, "PufferfishVengeance" );

		String durationComment = "Pufferfish negative effects duration per enchantment level. (in seconds)";
		this.durationConfig = new DurationConfig( "duration", durationComment, false, 4.0, 1.0, 30.0 );

		String hungerComment = "Amplifier level of Hunger effect.";
		this.hungerAmplifier = new IntegerConfig( "hunger", hungerComment, false, 2, 0, 10 );

		String poisonComment = "Amplifier level of Poison effect.";
		this.poisonAmplifier = new IntegerConfig( "poison", poisonComment, false, 3, 0, 10 );

		String nauseaComment = "Amplifier level of Nausea effect.";
		this.nauseaAmplifier = new IntegerConfig( "nausea", nauseaComment, false, 0, 0, 10 );

		this.enchantmentGroup.addConfigs( this.durationConfig, this.hungerAmplifier, this.poisonAmplifier, this.nauseaAmplifier );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 5 + level * 12 ) );
	}

	@Override
	public boolean canEnchant( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem || super.canEnchant( stack );
	}

	/** Event that checks if attacker has an appropriate enchantment level. */
	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		PufferfishVengeanceEnchantment pufferfishVengeance = Instances.PUFFERFISH_VENGEANCE;
		DamageSource damageSource = event.getSource();
		LivingEntity attacker = CommonHelper.castIfPossible( LivingEntity.class, damageSource.getDirectEntity() );
		if( attacker == null || !pufferfishVengeance.hasEnchantment( attacker ) )
			return;

		LivingEntity target = event.getEntityLiving();
		pufferfishVengeance.applyEffects( target );

		attacker.level.playSound( null, target.getX(), target.getY(), target.getZ(), SoundEvents.PUFFER_FISH_BLOW_OUT, SoundSource.AMBIENT,
			1.0f, 1.0f
		);
	}

	/**
	 Applying negative effects on given target.

	 @param target Entity on which negative effects will be applied.
	 */
	protected void applyEffects( LivingEntity target ) {
		int durationInTicks = this.durationConfig.getDuration() + TimeConverter.secondsToTicks( 1.0 );

		EffectHelper.applyEffectIfPossible( target, MobEffects.HUNGER, durationInTicks, this.hungerAmplifier.get() );
		EffectHelper.applyEffectIfPossible( target, MobEffects.POISON, durationInTicks, this.poisonAmplifier.get() );
		EffectHelper.applyEffectIfPossible( target, MobEffects.CONFUSION, durationInTicks, this.nauseaAmplifier.get() );
	}
}
