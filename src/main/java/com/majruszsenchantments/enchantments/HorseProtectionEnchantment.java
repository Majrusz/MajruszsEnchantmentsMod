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

public class HorseProtectionEnchantment extends CustomEnchantment {
	public HorseProtectionEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 - 5 )
			.maxLevelCost( level->level * 6 + 5 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< HorseProtectionEnchantment > {
		static final AttributeHandler ARMOR_ATTRIBUTE = new AttributeHandler( "f7f6f46b-23a1-4d3b-8e83-3160c6390f9a", "HorseProtectionBonus", Attributes.ARMOR, AttributeModifier.Operation.ADDITION );
		final DoubleConfig armorBonus = new DoubleConfig( 2.0, new Range<>( 1.0, 10.0 ) );

		public Modifier() {
			super( Registries.HORSE_PROTECTION, Registries.Modifiers.ENCHANTMENT );

			new OnEquipmentChanged.Context( this::updateSpeed )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( data->data.entity instanceof Animal ) // checks for the animal class instead of horse to have a compatibility with other mods
				.addConfig( this.armorBonus.name( "armor_bonus" ).comment( "Armor bonus per enchantment level." ) )
				.insertTo( this );

			this.name( "ArmoredCaravan" ).comment( "Increases the horse's armor." );
		}

		private void updateSpeed( OnEquipmentChanged.Data data ) {
			float extraArmor = this.armorBonus.asFloat() * this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR );
			ARMOR_ATTRIBUTE.setValueAndApply( data.entity, extraArmor );
		}
	}
}
