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
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseProtectionEnchantment extends CustomEnchantment {
	public static Supplier< HorseProtectionEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.HORSE_ARMOR, EquipmentSlots.ARMOR, false, 4, level->-5 + 6 * level, level->5 + 6 * level );
		HorseProtectionEnchantment enchantment = new HorseProtectionEnchantment( params );
		Modifier modifier = new HorseProtectionEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public HorseProtectionEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< HorseProtectionEnchantment > {
		static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "f7f6f46b-23a1-4d3b-8e83-3160c6390f9a", "HorseProtectionBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
		final DoubleConfig armorBonus = new DoubleConfig( "armor_bonus", "Armor bonus per enchantment level.", false, 2.0, 1.0, 10.0 );

		public Modifier( HorseProtectionEnchantment enchantment ) {
			super( enchantment, "ArmoredCaravan", "Increases the horse's armor." );

			OnEquipmentChanged.Context onChange = new OnEquipmentChanged.Context( this::updateSpeed );
			onChange.addCondition( new Condition.IsServer() )
				.addCondition( data->data.entity instanceof Animal ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.armorBonus );

			this.addContext( onChange );
		}

		private void updateSpeed( OnEquipmentChanged.Data data ) {
			float extraArmor = this.armorBonus.asFloat() * this.enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			ARMOR_ATTRIBUTE.setValueAndApply( data.entity, extraArmor );
		}
	}
}
