package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.level.LevelHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class HorseFrostWalkerEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment() {
			@Override
			public boolean isTreasureOnly() {
				return true;
			}
		}
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 15 );
	}

	public HorseFrostWalkerEnchantment() {
		super( MajruszsEnchantments.HORSE_FROST_WALKER, HorseFrostWalkerEnchantment.class, false );

		OnEntityTicked.listen( this::freezeNearbyWater )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 0.1f ) )
			.addCondition( data->data.entity instanceof Animal )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) );

		OnEntityPreDamaged.listen( OnEntityPreDamaged::cancelDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.target ) )
			.addCondition( Condition.predicate( data->data.source.equals( DamageSource.HOT_FLOOR ) ) )
			.addCondition( Condition.predicate( data->data.target instanceof Animal ) );
	}

	private void freezeNearbyWater( OnEntityTicked data ) {
		LevelHelper.freezeWater( data.entity, EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) + 2, 60, 120, false );
	}
}
