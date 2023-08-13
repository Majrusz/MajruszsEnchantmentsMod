package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.contexts.OnEquipmentChanged;
import com.mlib.math.Range;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseProtectionEnchantment extends CustomEnchantment {
	public HorseProtectionEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 - 5 )
			.maxLevelCost( level->level * 6 + 5 );
	}

	@AutoInstance
	public static class Handler {
		static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "f7f6f46b-23a1-4d3b-8e83-3160c6390f9a", "HorseProtectionBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
		final DoubleConfig armorBonus = new DoubleConfig( 2.0, new Range<>( 1.0, 10.0 ) );
		final Supplier< HorseProtectionEnchantment > enchantment = Registries.HORSE_PROTECTION;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "ArmoredCaravan" )
				.comment( "Increases the horse's armor." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEquipmentChanged.listen( this::updateSpeed )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.entity instanceof Animal ) ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.armorBonus.name( "armor_bonus" ).comment( "Armor bonus per enchantment level." ) )
				.insertTo( group );
		}

		private void updateSpeed( OnEquipmentChanged.Data data ) {
			float extraArmor = this.armorBonus.asFloat() * this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			ARMOR_ATTRIBUTE.setValue( extraArmor ).apply( data.entity );
		}
	}
}
