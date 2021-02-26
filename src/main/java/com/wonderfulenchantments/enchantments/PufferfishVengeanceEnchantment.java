package com.wonderfulenchantments.enchantments;

import com.mlib.TimeConverter;
import com.mlib.config.DurationConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.effects.EffectHelper;
import com.wonderfulenchantments.Instances;
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

/** Enchantment that inflicts on the enemy several negative effects. */
@Mod.EventBusSubscriber
public class PufferfishVengeanceEnchantment extends WonderfulEnchantment {
	protected final DurationConfig durationConfig;
	protected final IntegerConfig hungerAmplifier, poisonAmplifier, nauseaAmplifier;

	public PufferfishVengeanceEnchantment() {
		super( "pufferfish_vengeance", Rarity.RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, "PufferfishVengeance" );
		String durationComment = "Pufferfish negative effects duration per enchantment level. (in seconds)";
		String hungerComment = "Amplifier level of Hunger effect.";
		String poisonComment = "Amplifier level of Poison effect.";
		String nauseaComment = "Amplifier level of Nausea effect.";
		this.durationConfig = new DurationConfig( "duration", durationComment, false, 4.0, 1.0, 30.0 );
		this.hungerAmplifier = new IntegerConfig( "hunger", hungerComment, false, 2, 0, 10 );
		this.poisonAmplifier = new IntegerConfig( "poison", poisonComment, false, 3, 0, 10 );
		this.nauseaAmplifier = new IntegerConfig( "nausea", nauseaComment, false, 0, 0, 10 );
		this.enchantmentGroup.addConfigs( this.durationConfig, this.hungerAmplifier, this.poisonAmplifier, this.nauseaAmplifier );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 5 + level * 12 ) );
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
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( Instances.PUFFERFISH_VENGEANCE, attacker );

		if( enchantmentLevel <= 0 )
			return;

		LivingEntity target = event.getEntityLiving();
		World world = attacker.getEntityWorld();

		Instances.PUFFERFISH_VENGEANCE.applyEffects( target );
		world.playSound( null, target.getPosX(), target.getPosY(), target.getPosZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.AMBIENT,
			1.0f, 1.0f
		);
	}

	/**
	 Applying negative effects on given target.

	 @param target Entity on which negative effects will be applied.
	 */
	protected void applyEffects( LivingEntity target ) {
		int durationInTicks = this.durationConfig.getDuration() + TimeConverter.secondsToTicks( 1.0 );

		EffectHelper.applyEffectIfPossible( target, Effects.HUNGER, durationInTicks, this.hungerAmplifier.get() );
		EffectHelper.applyEffectIfPossible( target, Effects.POISON, durationInTicks, this.poisonAmplifier.get() );
		EffectHelper.applyEffectIfPossible( target, Effects.NAUSEA, durationInTicks, this.nauseaAmplifier.get() );
	}
}
