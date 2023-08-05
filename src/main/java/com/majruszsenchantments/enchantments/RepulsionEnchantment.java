package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnDamaged;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.items.ItemHelper;
import com.mlib.math.Range;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class RepulsionEnchantment extends CustomEnchantment {
	public RepulsionEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.SHIELD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.maxLevel( 1 )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig strength = new DoubleConfig( 1.0, new Range<>( 0.0, 10.0 ) );
		final Supplier< RepulsionEnchantment > enchantment = Registries.REPULSION;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Repulsion" )
				.comment( "Knocks back mobs when blocking their attack." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnDamaged.listen( this::knockbackEnemy )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.attacker != null ) )
				.addCondition( OnDamaged.isDirect() )
				.addCondition( OnDamaged.dealtAnyDamage().negate() )
				.addCondition( this.isBlockingWithRepulsionShield() )
				.addConfig( this.strength.name( "strength" ).comment( "Determines how strong the knock back is." ) )
				.insertTo( group );
		}

		private void knockbackEnemy( OnDamaged.Data data ) {
			data.attacker.knockback( this.strength.asFloat(), Mth.sin( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ), -Mth.cos( data.attacker.getYRot() * ( float )Math.PI / 180.0f + ( float )Math.PI ) );
		}

		private Condition< OnDamaged.Data > isBlockingWithRepulsionShield() {
			return Condition.predicate( data->this.enchantment.get().hasEnchantment( ItemHelper.getCurrentlyUsedItem( data.target ) ) );
		}
	}
}
