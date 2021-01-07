package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
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

	protected static boolean absorbSucceed( ItemStack itemStack ) {
		return itemStack.getItem() instanceof ShieldItem && itemStack.getUseAction() == UseAction.BLOCK;
	}

	protected static void damageShield( ItemStack shield, LivingEntity entity, EffectInstance effectInstance ) {
		double amplifierDamage = effectInstance.getAmplifier();
		double durationDamage = ( ( double )effectInstance.getDuration() ) / WonderfulEnchantmentHelper.secondsToTicks( 60.0 );

		shield.damageItem( ( int )( amplifierDamage + durationDamage + 1.0 ), entity, ( e )->e.sendBreakAnimation( shield.getEquipmentSlot() ) );
	}
}
