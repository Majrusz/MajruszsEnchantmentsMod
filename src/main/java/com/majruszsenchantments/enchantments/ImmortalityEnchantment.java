package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDeath;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class ImmortalityEnchantment extends CustomEnchantment {
	public ImmortalityEnchantment() {
		this.rarity( Rarity.RARE )
			.category( Registries.SHIELD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.minLevelCost( level->20 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< ImmortalityEnchantment > {
		public Modifier() {
			super( Registries.IMMORTALITY, Registries.Modifiers.ENCHANTMENT );

			new OnDeath.Context( this::cancelDeath )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEnchantment<>( enchantment ) )
				.insertTo( this );

			this.name( "Immortality" ).comment( "Cheats death on a fatal hit at the cost of this enchantment." );
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
