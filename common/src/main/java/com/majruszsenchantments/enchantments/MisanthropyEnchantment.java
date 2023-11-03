package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.contexts.base.Condition;
import com.mlib.entity.EntityHelper;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class MisanthropyEnchantment extends Handler {
	float damage = 2.5f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( MajruszsEnchantments.IS_MELEE_MINECRAFT )
			.slots( EquipmentSlots.MAINHAND )
			.maxLevel( 5 )
			.minLevelCost( level->level * 8 - 3 )
			.maxLevelCost( level->level * 8 + 17 )
			.compatibility( enchantment->!( enchantment instanceof DamageEnchantment ) );
	}

	public MisanthropyEnchantment() {
		super( MajruszsEnchantments.MISANTHROPY, false );

		OnEntityPreDamaged.listen( this::increaseDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EntityHelper.isHuman( data.target ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.attacker ) );

		this.config.defineFloat( "damage_bonus_per_level", ()->this.damage, x->this.damage = Range.of( 0.0f, 100.0f ).clamp( x ) );
	}

	private void increaseDamage( OnEntityPreDamaged data ) {
		data.damage += EnchantmentHelper.getLevel( this.enchantment, data.attacker ) * this.damage;
		data.spawnMagicParticles = true;
	}
}
