package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.levels.LevelHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;

public class HorseFrostWalkerEnchantment extends CustomEnchantment {
	public HorseFrostWalkerEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.HORSE_ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 15 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< HorseFrostWalkerEnchantment > {
		public Modifier() {
			super( Registries.HORSE_FROST_WALKER, Registries.Modifiers.ENCHANTMENT );

			new OnEntityTick.Context( this::freezeNearbyWater )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( data->data.entity instanceof Animal )
				.insertTo( this );

			new OnPreDamaged.Context( OnPreDamaged.CANCEL )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( data->DamageSource.HOT_FLOOR.equals( data.source ) )
				.addCondition( data->data.entity instanceof Animal )
				.insertTo( this );

			this.name( "HorseFrostWalker" ).comment( "Creates a path of ice when walking over water on a horse." );
		}

		private void freezeNearbyWater( OnEntityTick.Data data ) {
			assert data.entity != null;
			int radius = this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) + 2;
			LevelHelper.freezeWater( data.entity, radius, 60, 120, false );
		}
	}
}
