package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.math.Range;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class MisanthropyEnchantment extends CustomEnchantment {
	public MisanthropyEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.MELEE_MINECRAFT )
			.slots( EquipmentSlots.MAINHAND )
			.maxLevel( 5 )
			.minLevelCost( level->level * 8 - 3 )
			.maxLevelCost( level->level * 8 + 17 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DamageEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< MisanthropyEnchantment > {
		final DoubleConfig damageBonus = new DoubleConfig( 2.5, new Range<>( 1.0, 10.0 ) );

		public Modifier() {
			super( Registries.MISANTHROPY, Registries.Modifiers.ENCHANTMENT );

			new OnPreDamaged.Context( this::increaseDamage )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.attacker ) )
				.addCondition( data->EntityHelper.isHuman( data.target ) )
				.addConfig( this.damageBonus.name( "damage_bonus" ).comment( "Extra damage dealt to humans per enchantment level." ) )
				.insertTo( this );

			this.name( "Misanthropy" ).comment( "Increases the damage against villagers, pillagers, witches and other players." );
		}

		private void increaseDamage( OnPreDamaged.Data data ) {
			data.extraDamage += this.enchantment.get().getEnchantmentLevel( data.attacker ) * this.damageBonus.asFloat();
			data.spawnMagicParticles = true;
		}
	}
}
