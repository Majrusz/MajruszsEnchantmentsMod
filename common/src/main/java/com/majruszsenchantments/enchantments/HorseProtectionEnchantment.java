package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnItemEquipped;
import com.mlib.contexts.base.Condition;
import com.mlib.entity.AttributeHandler;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Range;
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
		super( MajruszsEnchantments.HORSE_PROTECTION, false );

		this.attribute = new AttributeHandler( "%s_armor".formatted( this.enchantment.getId() ), ()->Attributes.ARMOR, AttributeModifier.Operation.ADDITION );

		OnItemEquipped.listen( this::updateArmor )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.entity instanceof Animal ); // checks for the animal class instead of horse to have a compatibility with other mods

		this.config.defineFloat( "armor_bonus_per_level", s->this.armor, ( s, v )->this.armor = Range.of( 0.0f, 100.0f ).clamp( v ) );
	}

	private void updateArmor( OnItemEquipped data ) {
		this.attribute.setValue( this.armor * EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) ).apply( data.entity );
	}
}
