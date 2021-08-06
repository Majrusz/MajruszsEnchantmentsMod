package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.TimeConverter;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that causes the shield to absorb all negative effects at the expense of durability. */
@Mod.EventBusSubscriber
public class AbsorberEnchantment extends WonderfulEnchantment {
	protected final DurationConfig minimumEffectDuration;
	protected final StringListConfig forbiddenEffects;

	public AbsorberEnchantment() {
		super( "absorber", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlots.BOTH_HANDS, "Absorber" );

		String durationComment = "Minimum required duration to absorb an effect. (in seconds)";
		this.minimumEffectDuration = new DurationConfig( "minimum_duration", durationComment, false, 2.5, 0.0, 60.0 );

		String effectsComment = "MobEffects that can not be absorbed.";
		this.forbiddenEffects = new StringListConfig( "forbidden_effects", effectsComment, false, "majruszs_difficulty:bleeding" );

		this.enchantmentGroup.addConfigs( this.minimumEffectDuration, this.forbiddenEffects );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 15 );
		setMinimumEnchantabilityCalculator( level->( 5 + 8 * level ) );
	}

	/** Event that manages whether or not an effect should be applied. */
	@SubscribeEvent
	public static void onApplyingEffect( PotionEvent.PotionApplicableEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		MobEffectInstance effectInstance = event.getPotionEffect();
		MobEffect effect = effectInstance.getEffect();
		AbsorberEnchantment absorber = Instances.ABSORBER;

		if( absorber.isForbidden( effect ) || effect.isBeneficial() || effectInstance.getDuration() < absorber.minimumEffectDuration.getDuration() )
			return;

		for( EquipmentSlot equipmentSlotType : EquipmentSlots.BOTH_HANDS ) {
			ItemStack itemStack = entity.getItemBySlot( equipmentSlotType );

			if( !absorbSucceed( itemStack ) )
				continue;

			damageShield( itemStack, entity, effectInstance );

			event.setResult( Event.Result.DENY );
			entity.level.playSound( null, entity.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.AMBIENT, 0.5f, 0.8f );
			break;
		}
	}

	/**
	 Checking that the item is the shield and has the appropriate enchantment.

	 @param itemStack Item stack to check.
	 */
	protected static boolean absorbSucceed( ItemStack itemStack ) {
		int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel( Instances.ABSORBER, itemStack );

		return itemStack.getItem() instanceof ShieldItem && itemStack.getUseAnimation() == UseAnim.BLOCK && enchantmentLevel > 0;
	}

	/**
	 Damaging the shield when the effect is absorbed.

	 @param shield         Shield to be damaged.
	 @param entity         Entity which is holding the shield.
	 @param effectInstance MobEffect that was absorbed, required to calculate the damage.
	 */
	protected static void damageShield( ItemStack shield, LivingEntity entity, MobEffectInstance effectInstance ) {
		double amplifierDamage = effectInstance.getAmplifier();
		double durationDamage = ( ( double )effectInstance.getDuration() ) / TimeConverter.secondsToTicks( 60.0 );

		EquipmentSlot slotType = entity.getMainHandItem() == shield ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		shield.hurtAndBreak( ( int )( amplifierDamage + durationDamage + 1.0 ), entity, e->e.broadcastBreakEvent( slotType ) );
	}

	/** Checks whether given effect is not forbidden. (is not disabled by player) */
	protected boolean isForbidden( MobEffect effect ) {
		ResourceLocation effectRegistryName = effect.getRegistryName();

		return effectRegistryName != null && this.forbiddenEffects.contains( effectRegistryName.toString() );
	}
}
