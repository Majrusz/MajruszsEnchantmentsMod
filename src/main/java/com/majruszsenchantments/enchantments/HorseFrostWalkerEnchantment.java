package com.majruszsenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.contexts.OnPreDamagedContext;
import com.mlib.gamemodifiers.data.OnEntityTickData;
import com.mlib.gamemodifiers.data.OnPreDamagedData;
import com.mlib.levels.LevelHelper;
import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;

import java.util.function.Supplier;

public class HorseFrostWalkerEnchantment extends CustomEnchantment {
	public static Supplier< HorseFrostWalkerEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.HORSE_ARMOR, EquipmentSlots.ARMOR, false, 2, level->10 * level, level->15 + 10 * level );
		HorseFrostWalkerEnchantment enchantment = new HorseFrostWalkerEnchantment( params );
		Modifier modifier = new HorseFrostWalkerEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public HorseFrostWalkerEnchantment( Parameters params ) {
		super( params );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	private static class Modifier extends EnchantmentModifier< HorseFrostWalkerEnchantment > {
		public Modifier( HorseFrostWalkerEnchantment enchantment ) {
			super( enchantment, "HorseFrostWalker", "Creates a path of ice when walking over water on a horse." );

			OnEntityTickContext onTick = new OnEntityTickContext( this::freezeNearbyWater );
			onTick.addCondition( new Condition.IsServer() )
				.addCondition( new Condition.HasEnchantment( enchantment ) )
				.addCondition( data->data.entity instanceof Animal );

			OnPreDamagedContext onPreDamaged = new OnPreDamagedContext( this::disableDamage );
			onPreDamaged.addCondition( new Condition.IsServer() )
				.addCondition( new Condition.HasEnchantment( enchantment ) )
				.addCondition( data->DamageSource.HOT_FLOOR.equals( data.source ) )
				.addCondition( data->data.entity instanceof Animal );

			this.addContexts( onTick, onPreDamaged );
		}

		private void freezeNearbyWater( OnEntityTickData data ) {
			assert data.entity != null;
			int radius = this.enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) + 2;
			LevelHelper.freezeWater( data.entity, radius, 60, 120, false );
		}

		private void disableDamage( OnPreDamagedData data ) {
			data.event.setCanceled( true );
		}
	}
}
