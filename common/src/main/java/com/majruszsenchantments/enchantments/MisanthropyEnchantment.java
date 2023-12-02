package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
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
		super( MajruszsEnchantments.MISANTHROPY, MisanthropyEnchantment.class, false );

		OnEntityPreDamaged.listen( this::increaseDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EntityHelper.isHuman( data.target ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.attacker ) );

		this.config.define( "damage_bonus_per_level", Reader.number(), s->this.damage, ( s, v )->this.damage = Range.of( 0.0f, 100.0f ).clamp( v ) );
	}

	private void increaseDamage( OnEntityPreDamaged data ) {
		data.damage += EnchantmentHelper.getLevel( this.enchantment, data.attacker ) * this.damage;
		data.spawnMagicParticles = true;
	}
}
