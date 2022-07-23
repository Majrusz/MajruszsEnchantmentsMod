package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEquipmentChangedContext;
import com.mlib.gamemodifiers.data.OnEquipmentChangedData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseSwiftnessEnchantment extends CustomEnchantment {
	public static Supplier< HorseSwiftnessEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.HORSE_ARMOR, EquipmentSlots.ARMOR, false, 4, level->6 * level, level->15 + 6 * level );
		HorseSwiftnessEnchantment enchantment = new HorseSwiftnessEnchantment( params );
		Modifier modifier = new HorseSwiftnessEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public HorseSwiftnessEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< HorseSwiftnessEnchantment > {
		static final AttributeHandler SPEED_ATTRIBUTE = new AttributeHandler( "76c3bea2-7ef1-4c4b-b062-a12355120ee7", "SwiftnessBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE );
		final DoubleConfig speedBonus = new DoubleConfig( "speed_bonus", "Speed bonus multiplier per enchantment level.", false, 0.125, 0.01, 1.0 );

		public Modifier( HorseSwiftnessEnchantment enchantment ) {
			super( enchantment, "HorseSwiftness", "Increases the horse's movement speed." );

			OnEquipmentChangedContext onChange = new OnEquipmentChangedContext( this::updateSpeed );
			onChange.addCondition( new Condition.IsServer() )
				.addCondition( data->data.entity instanceof Animal ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.speedBonus );

			this.addContext( onChange );
		}

		private void updateSpeed( OnEquipmentChangedData data ) {
			float extraHealth = this.speedBonus.asFloat() * this.enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			SPEED_ATTRIBUTE.setValueAndApply( data.entity, extraHealth );
		}
	}
}
