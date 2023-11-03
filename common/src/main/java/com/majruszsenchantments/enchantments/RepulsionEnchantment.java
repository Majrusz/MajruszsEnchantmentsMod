package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityDamageBlocked;
import com.mlib.contexts.base.Condition;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.ItemHelper;
import com.mlib.math.Range;
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
		super( MajruszsEnchantments.REPULSION, false );

		OnEntityDamageBlocked.listen( this::knockback )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->!data.source.isIndirect() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, ItemHelper.getCurrentlyUsedItem( data.target ) ) );

		this.config.defineFloat( "knockback_strength", ()->this.strength, x->this.strength = Range.of( 0.0f, 100.0f ).clamp( x ) );
	}

	private void knockback( OnEntityDamageBlocked data ) {
		data.attacker.knockback( this.strength, Mth.sin( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ), -Mth.cos( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ) );
	}
}
