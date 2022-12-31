package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Utility;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntitySignalCheck;
import com.mlib.gamemodifiers.contexts.OnEntitySignalReceived;
import com.mlib.mobeffects.MobEffectHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SixthSenseEnchantment extends CustomEnchantment {
	public SixthSenseEnchantment() {
		this.rarity( Rarity.VERY_RARE )
			.category( EnchantmentCategory.ARMOR_HEAD )
			.slots( EquipmentSlots.HEAD )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< SixthSenseEnchantment > {
		public Modifier() {
			super( Registries.SIXTH_SENSE, Registries.Modifiers.ENCHANTMENT );

			new OnEntitySignalCheck.Context( OnEntitySignalCheck.DISPATCH )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.player ) )
				.addCondition( new Condition.IsShiftKeyDown<>( data->data.player ) )
				.insertTo( this );

			new OnEntitySignalReceived.Context( this::highlightEntity )
				.addCondition( data->data.owner instanceof LivingEntity )
				.insertTo( this );

			this.name( "SixthSense" ).comment( "Adds acquired items directly to player's inventory." );
		}

		private void highlightEntity( OnEntitySignalReceived.Data data ) {
			MobEffectHelper.tryToApply( ( LivingEntity )data.owner, MobEffects.GLOWING, Utility.secondsToTicks( 5.0 ), 0 );
		}
	}
}
