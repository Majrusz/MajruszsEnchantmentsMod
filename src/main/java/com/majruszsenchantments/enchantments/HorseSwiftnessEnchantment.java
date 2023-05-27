package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.math.Range;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseSwiftnessEnchantment extends CustomEnchantment {
	public HorseSwiftnessEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 )
			.maxLevelCost( level->level * 6 + 15 );
	}

	@AutoInstance
	public static class Handler {
		static final AttributeHandler SPEED_ATTRIBUTE = new AttributeHandler( "76c3bea2-7ef1-4c4b-b062-a12355120ee7", "HorseSwiftnessBonus", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE );
		final DoubleConfig speedBonus = new DoubleConfig( 0.125, new Range<>( 0.01, 1.0 ) );
		final Supplier< HorseSwiftnessEnchantment > enchantment = Registries.HORSE_SWIFTNESS;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "HorseSwiftness" )
				.comment( "Increases the horse's movement speed." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEquipmentChanged.listen( this::updateSpeed )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.entity instanceof Animal ) ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.speedBonus.name( "speed_bonus" ).comment( "Speed bonus multiplier per enchantment level." ) )
				.insertTo( group );
		}

		private void updateSpeed( OnEquipmentChanged.Data data ) {
			float extraSpeed = this.speedBonus.asFloat() * this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			SPEED_ATTRIBUTE.setValue( extraSpeed ).apply( data.entity );
		}
	}
}
