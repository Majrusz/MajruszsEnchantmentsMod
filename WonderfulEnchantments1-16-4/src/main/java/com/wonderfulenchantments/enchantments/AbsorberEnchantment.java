package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

/** Enchantment that causes the shield to absorb all negative effects at the expense of durability. */
@Mod.EventBusSubscriber
public class AbsorberEnchantment extends Enchantment {
	public AbsorberEnchantment() {
		super( Rarity.RARE, WonderfulEnchantmentHelper.SHIELD, EquipmentSlotTypes.BOTH_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 5 + 8 * level + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 15;
	}

	/** Event that manages whether or not an effect should be applied. */
	@SubscribeEvent
	public static void onApplyingEffect( PotionEvent.PotionApplicableEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		EffectInstance effectInstance = event.getPotionEffect();
		Effect effect = effectInstance.getPotion();

		if( effect.isBeneficial() )
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
		int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.ABSORBER.get(), itemStack );

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
		double durationDamage = ( ( double )effectInstance.getDuration() ) / WonderfulEnchantmentHelper.secondsToTicks( 60.0 );

		EquipmentSlotType slotType = entity.getHeldItemMainhand() == shield ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
		shield.damageItem( ( int )( amplifierDamage + durationDamage + 1.0 ), entity, ( e )->e.sendBreakAnimation( slotType ) );
	}
}
