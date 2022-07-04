package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnEquipmentChangedContext;
import com.mlib.gamemodifiers.contexts.OnPickupXpContext;
import com.mlib.gamemodifiers.data.OnEquipmentChangedData;
import com.mlib.gamemodifiers.data.OnPickupXpData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
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
		final DoubleConfig experienceMultiplier = new DoubleConfig( "experience_extra_multiplier", "Extra percent of experience acquired from all sources per enchantment level.", false, 0.2, 0.01, 10.0 );

		public Modifier( EnlightenmentEnchantment enchantment ) {
			super( enchantment, "Enlightenment", "Increases the experience gained from any source." );

			OnPickupXpContext onXpPickup = new OnPickupXpContext( this::increaseExperience );
			onXpPickup.addCondition( data->enchantment.hasEnchantment( data.player ) );

			OnEquipmentChangedContext onEquipmentChanged = new OnEquipmentChangedContext( this::giveAdvancement );
			onEquipmentChanged.addCondition( data->data.entity instanceof ServerPlayer )
				.addCondition( data->enchantment.getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) == 8 );

			this.addConfig( this.experienceMultiplier );
			this.addContexts( onXpPickup, onEquipmentChanged );
		}

		private void increaseExperience( OnPickupXpData data ) {
			int enlightenmentSum = this.enchantment.getEnchantmentSum( data.player, EquipmentSlots.ARMOR );
			int experiencePoints = Random.roundRandomly( enlightenmentSum * this.experienceMultiplier.get() * data.event.getOrb().getValue() );
			data.player.giveExperiencePoints( experiencePoints );
		}

		private void giveAdvancement( OnEquipmentChangedData data ) {
			Registries.BASIC_TRIGGER.trigger( ( ServerPlayer )data.entity, "enlightenment_8" );
		}
	}
}
