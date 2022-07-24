package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.entities.EntityHelper;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnDamagedContext;
import com.mlib.gamemodifiers.data.OnDamagedData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class ImmortalityEnchantment extends CustomEnchantment {
	public static Supplier< ImmortalityEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, Registries.SHIELD, EquipmentSlots.BOTH_HANDS, false, 1, level->20, level->50 );
		ImmortalityEnchantment enchantment = new ImmortalityEnchantment( params );
		Modifier modifier = new ImmortalityEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public ImmortalityEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< ImmortalityEnchantment > {
		public Modifier( ImmortalityEnchantment enchantment ) {
			super( enchantment, "Immortality", "Cheats death on a fatal hit at the cost of a shield." );

			OnDamagedContext onDamage = new OnDamagedContext( this::cancelDeath );
			onDamage.addCondition( new Condition.IsServer() )
				.addCondition( new Condition.HasEnchantment( enchantment ) )
				.addCondition( data->( data.target.getHealth() - data.event.getAmount() ) < 1.0f );

			this.addContexts( onDamage );
		}

		private void cancelDeath( OnDamagedData data ) {
			LivingEntity target = data.target;
			InteractionHand hand = this.enchantment.hasEnchantment( target.getMainHandItem() ) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			target.getItemInHand( hand ).hurtAndBreak( 9001, target, entity->entity.broadcastBreakEvent( hand ) );
			EntityHelper.cheatDeath( target, 1.0f, true );

			data.event.setCanceled( true );
		}
	}
}
