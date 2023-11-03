package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.level.LevelHelper;
import net.minecraft.world.damagesource.DamageTypes;
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
		super( MajruszsEnchantments.HORSE_FROST_WALKER, false );

		OnEntityTicked.listen( this::freezeNearbyWater )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
			.addCondition( Condition.predicate( data->data.entity instanceof Animal ) );

		OnEntityPreDamaged.listen( OnEntityPreDamaged::cancelDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.hasEnchantment( this.enchantment, data->data.target ) )
			.addCondition( Condition.predicate( data->data.source.is( DamageTypes.HOT_FLOOR ) ) )
			.addCondition( Condition.predicate( data->data.target instanceof Animal ) );
	}

	private void freezeNearbyWater( OnEntityTicked data ) {
		LevelHelper.freezeWater( data.entity, EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) + 2, 60, 120, false );
	}
}
