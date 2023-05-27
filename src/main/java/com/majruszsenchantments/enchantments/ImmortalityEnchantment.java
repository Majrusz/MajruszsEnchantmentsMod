package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnDeath;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class ImmortalityEnchantment extends CustomEnchantment {
	public ImmortalityEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.SHIELD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.minLevelCost( level->20 )
			.maxLevelCost( level->50 );
	}

	@AutoInstance
	public static class Handler {
		final Supplier< ImmortalityEnchantment > enchantment = Registries.IMMORTALITY;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Immortality" )
				.comment( "Cheats death on a fatal hit at the cost of this enchantment." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnDeath.listen( this::cancelDeath )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.target ) )
				.insertTo( group );
		}

		private void cancelDeath( OnDeath.Data data ) {
			LivingEntity target = data.target;
			InteractionHand hand = this.enchantment.get().hasEnchantment( target.getMainHandItem() ) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			this.enchantment.get().removeEnchantment( target.getItemInHand( hand ) );
			EntityHelper.cheatDeath( target, 1.0f, true );

			data.event.setCanceled( true );
		}
	}
}
