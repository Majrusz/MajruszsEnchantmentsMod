package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.gamemodifiers.contexts.OnPickupXp;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class EnlightenmentEnchantment extends CustomEnchantment {
	public static Supplier< EnlightenmentEnchantment > create() {
		Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, false, 2, level->6 + 12 * level, level->26 + 12 * level );
		EnlightenmentEnchantment enchantment = new EnlightenmentEnchantment( params );
		Modifier modifier = new EnlightenmentEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public EnlightenmentEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< EnlightenmentEnchantment > {
		final DoubleConfig experienceMultiplier = new DoubleConfig( "experience_extra_multiplier", "Extra percent of experience acquired from all sources per enchantment level.", false, 0.25, 0.01, 10.0 );

		public Modifier( EnlightenmentEnchantment enchantment ) {
			super( enchantment, "Enlightenment", "Increases the experience gained from any source." );

			OnPickupXp.Context onXpPickup = new OnPickupXp.Context( this::increaseExperience );
			onXpPickup.addCondition( new Condition.HasEnchantment<>( enchantment ) );

			OnEquipmentChanged.Context onEquipmentChanged = new OnEquipmentChanged.Context( this::giveAdvancement );
			onEquipmentChanged.addCondition( data->data.entity instanceof ServerPlayer )
				.addCondition( data->enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) == 8 );

			this.addConfig( this.experienceMultiplier );
			this.addContexts( onXpPickup, onEquipmentChanged );
		}

		private void increaseExperience( OnPickupXp.Data data ) {
			int enlightenmentSum = this.enchantment.getEnchantmentSum( data.player, EquipmentSlots.ARMOR );
			int experiencePoints = Random.roundRandomly( enlightenmentSum * this.experienceMultiplier.get() * data.event.getOrb().getValue() );
			if( experiencePoints > 1 ) {
				data.player.giveExperiencePoints( Random.nextInt( 1, experiencePoints ) );
			} else if( experiencePoints > 0 ) {
				data.player.giveExperiencePoints( 1 );
			}
		}

		private void giveAdvancement( OnEquipmentChanged.Data data ) {
			Registries.BASIC_TRIGGER.trigger( ( ServerPlayer )data.entity, "enlightenment_8" );
		}
	}
}
