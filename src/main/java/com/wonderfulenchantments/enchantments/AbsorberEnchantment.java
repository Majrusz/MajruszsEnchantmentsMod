package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.TimeConverter;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Enchantment that causes the shield to absorb all negative effects at the expense of durability. */
@Mod.EventBusSubscriber
public class AbsorberEnchantment extends WonderfulEnchantment {
	protected final DurationConfig minimumEffectDuration;
	protected final StringListConfig forbiddenEffects;

	public AbsorberEnchantment() {
		super( "absorber", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlotTypes.BOTH_HANDS, "Absorber" );
		String duration_comment = "Minimum required duration to absorb an effect. (in seconds)";
		String effects_comment = "Effects that can not be absorbed.";
		this.minimumEffectDuration = new DurationConfig( "minimum_duration", duration_comment, false, 2.5, 0.0, 60.0 );
		this.forbiddenEffects = new StringListConfig( "forbidden_effects", effects_comment, false, "majruszs_difficulty:bleeding" );
		this.enchantmentGroup.addConfigs( this.minimumEffectDuration, this.forbiddenEffects );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 15 );
		setMinimumEnchantabilityCalculator( level->( 5 + 8 * level ) );
	}

	/** Event that manages whether or not an effect should be applied. */
	@SubscribeEvent
	public static void onApplyingEffect( PotionEvent.PotionApplicableEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		EffectInstance effectInstance = event.getPotionEffect();
		Effect effect = effectInstance.getPotion();
		AbsorberEnchantment absorber = Instances.ABSORBER;

		if( absorber.isForbidden( effect ) || effect.isBeneficial() || effectInstance.getDuration() < absorber.minimumEffectDuration.getDuration() )
			return;

		for( EquipmentSlotType equipmentSlotType : EquipmentSlotTypes.BOTH_HANDS ) {
			ItemStack itemStack = entity.getItemStackFromSlot( equipmentSlotType );

			if( !absorbSucceed( itemStack ) )
				continue;

			damageShield( itemStack, entity, effectInstance );

			event.setResult( Event.Result.DENY );
			entity.world.playSound( null, entity.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.AMBIENT, 0.5f, 0.8f );
			break;
		}
	}

	/**
	 Checking that the item is the shield and has the appropriate enchantment.

	 @param itemStack Item stack to check.
	 */
	protected static boolean absorbSucceed( ItemStack itemStack ) {
		int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( Instances.ABSORBER, itemStack );

		return itemStack.getItem() instanceof ShieldItem && itemStack.getUseAction() == UseAction.BLOCK && enchantmentLevel > 0;
	}

	/**
	 Damaging the shield when the effect is absorbed.

	 @param shield         Shield to be damaged.
	 @param entity         Entity which is holding the shield.
	 @param effectInstance Effect that was absorbed, required to calculate the damage.
	 */
	protected static void damageShield( ItemStack shield, LivingEntity entity, EffectInstance effectInstance ) {
		double amplifierDamage = effectInstance.getAmplifier();
		double durationDamage = ( ( double )effectInstance.getDuration() ) / TimeConverter.secondsToTicks( 60.0 );

		EquipmentSlotType slotType = entity.getHeldItemMainhand() == shield ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		shield.damageItem( ( int )( amplifierDamage + durationDamage + 1.0 ), entity, e->e.sendBreakAnimation( slotType ) );
	}

	/** Checks whether given effect is not forbidden. (is not disabled by player) */
	protected boolean isForbidden( Effect effect ) {
		ResourceLocation effectRegistryName = effect.getRegistryName();

		return effectRegistryName != null && this.forbiddenEffects.contains( effectRegistryName.toString() );
	}
}
