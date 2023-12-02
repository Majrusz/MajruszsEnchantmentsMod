package com.majruszsenchantments.enchantments;

import com.majruszlibrary.data.Reader;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnItemEquipped;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Range;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class HorseSwiftnessEnchantment extends Handler {
	final AttributeHandler attribute;
	float multiplier = 0.125f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 )
			.maxLevelCost( level->level * 6 + 15 );
	}

	public HorseSwiftnessEnchantment() {
		super( MajruszsEnchantments.HORSE_SWIFTNESS, HorseSwiftnessEnchantment.class, false );

		this.attribute = new AttributeHandler( "%s_speed".formatted( this.enchantment.getId() ), ()->Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_BASE );

		OnItemEquipped.listen( this::updateSpeed )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.entity instanceof Animal ); // checks for the animal class instead of horse to have a compatibility with other mods

		this.config.define( "speed_multiplier_per_level", Reader.number(), s->this.multiplier, ( s, v )->this.multiplier = Range.of( 0.0f, 10.0f ).clamp( v ) );
	}

	private void updateSpeed( OnItemEquipped data ) {
		this.attribute.setValue( this.multiplier * EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) ).apply( data.entity );
	}
}
