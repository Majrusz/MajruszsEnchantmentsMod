package com.majruszsenchantments.enchantments;

import com.majruszlibrary.data.Reader;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnEntityDamageBlocked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.math.Range;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class RepulsionEnchantment extends Handler {
	float strength = 1.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( MajruszsEnchantments.IS_SHIELD )
			.slots( EquipmentSlots.HANDS )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	public RepulsionEnchantment() {
		super( MajruszsEnchantments.REPULSION, RepulsionEnchantment.class, false );

		OnEntityDamageBlocked.listen( this::knockback )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!data.source.isIndirect() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, ItemHelper.getCurrentlyUsedItem( data.target ) ) );

		this.config.define( "knockback_strength", Reader.number(), s->this.strength, ( s, v )->this.strength = Range.of( 0.0f, 100.0f ).clamp( v ) );
	}

	private void knockback( OnEntityDamageBlocked data ) {
		data.attacker.knockback( this.strength, Mth.sin( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ), -Mth.cos( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ) );
	}
}
