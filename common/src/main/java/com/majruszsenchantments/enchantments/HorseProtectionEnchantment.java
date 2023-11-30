package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnItemEquipped;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class HorseProtectionEnchantment extends Handler {
	final AttributeHandler attribute;
	float armor = 2.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 4 )
			.minLevelCost( level->level * 6 - 5 )
			.maxLevelCost( level->level * 6 + 5 );
	}

	public HorseProtectionEnchantment() {
		super( MajruszsEnchantments.HORSE_PROTECTION, HorseProtectionEnchantment.class, false );

		this.attribute = new AttributeHandler( "%s_armor".formatted( this.enchantment.getId() ), ()->Attributes.ARMOR, AttributeModifier.Operation.ADDITION );

		OnItemEquipped.listen( this::updateArmor )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.entity instanceof Animal ); // checks for the animal class instead of horse to have a compatibility with other mods

		this.config.define( "armor_bonus_per_level", Reader.number(), s->this.armor, ( s, v )->this.armor = Range.of( 0.0f, 100.0f ).clamp( v ) );
	}

	private void updateArmor( OnItemEquipped data ) {
		this.attribute.setValue( this.armor * EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) ).apply( data.entity );
	}
}
