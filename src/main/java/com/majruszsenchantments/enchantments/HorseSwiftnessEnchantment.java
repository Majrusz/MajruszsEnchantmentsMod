package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.math.Range;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;

public class HorseSwiftnessEnchantment extends CustomEnchantment {
	public HorseSwiftnessEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 )
			.maxLevelCost( level->level * 6 + 15 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< HorseSwiftnessEnchantment > {
		static final AttributeHandler SPEED_ATTRIBUTE = new AttributeHandler( "76c3bea2-7ef1-4c4b-b062-a12355120ee7", "HorseSwiftnessBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE );
		final DoubleConfig speedBonus = new DoubleConfig( 0.125, new Range<>( 0.01, 1.0 ) );

		public Modifier() {
			super( Registries.HORSE_SWIFTNESS, Registries.Modifiers.ENCHANTMENT );

			new OnEquipmentChanged.Context( this::updateSpeed )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( data->data.entity instanceof Animal ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.speedBonus.name( "speed_bonus" ).comment( "Speed bonus multiplier per enchantment level." ) )
				.insertTo( this );

			this.name( "HorseSwiftness" ).comment( "Increases the horse's movement speed." );
		}

		private void updateSpeed( OnEquipmentChanged.Data data ) {
			float extraSpeed = this.speedBonus.asFloat() * this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			SPEED_ATTRIBUTE.setValueAndApply( data.entity, extraSpeed );
		}
	}
}
