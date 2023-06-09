package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseFrostWalkerEnchantment extends CustomEnchantment {
	public HorseFrostWalkerEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 15 );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@AutoInstance
	public static class Handler {
		final Supplier< HorseFrostWalkerEnchantment > enchantment = Registries.HORSE_FROST_WALKER;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "HorseFrostWalker" )
				.comment( "Creates a path of ice when walking over water on a horse." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEntityTick.listen( this::freezeNearbyWater )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addCondition( Condition.predicate( data->data.entity instanceof Animal ) )
				.insertTo( group );

			OnPreDamaged.listen( OnPreDamaged.CANCEL )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.target ) )
				.addCondition( Condition.predicate( data->data.source.is( DamageTypes.HOT_FLOOR ) ) )
				.addCondition( Condition.predicate( data->data.target instanceof Animal ) )
				.insertTo( group );
		}

		private void freezeNearbyWater( OnEntityTick.Data data ) {
			assert data.entity != null;
			int radius = this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) + 2;
			LevelHelper.freezeWater( data.entity, radius, 60, 120, false );
		}
	}
}
