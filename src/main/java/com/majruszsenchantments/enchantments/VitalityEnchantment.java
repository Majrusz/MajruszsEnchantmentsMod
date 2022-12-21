package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;

public class VitalityEnchantment extends CustomEnchantment {
	public static Supplier< VitalityEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.SHIELD, EquipmentSlots.BOTH_HANDS, false, 3, level->-5 + 8 * level, level->15 + 8 * level );
		VitalityEnchantment enchantment = new VitalityEnchantment( params );
		Modifier modifier = new VitalityEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public VitalityEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< VitalityEnchantment > {
		static final AttributeHandler HEALTH_ATTRIBUTE = new AttributeHandler( "575cb29a-1ee4-11eb-adc1-0242ac120002", "VitalityBonus", Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION );
		final DoubleConfig healthBonus = new DoubleConfig( "health_bonus", "Health bonus per enchantment level.", false, 2.0, 1.0, 10.0 );

		public Modifier( VitalityEnchantment enchantment ) {
			super( enchantment, "Vitality", "Increases the player's health." );

			OnEquipmentChanged.Context onChange = new OnEquipmentChanged.Context( this::updateHealth );
			onChange.addCondition( new Condition.IsServer<>() ).addConfig( this.healthBonus );

			this.addContext( onChange );
		}

		private void updateHealth( OnEquipmentChanged.Data data ) {
			float extraHealth = this.healthBonus.asFloat() * this.enchantment.getEnchantmentSum( data.entity, EquipmentSlots.BOTH_HANDS );
			HEALTH_ATTRIBUTE.setValueAndApply( data.entity, extraHealth );
		}
	}
}
