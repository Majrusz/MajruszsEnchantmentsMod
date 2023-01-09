package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.gamemodifiers.contexts.OnPickupXp;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnlightenmentEnchantment extends CustomEnchantment {
	public EnlightenmentEnchantment() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 12 + 6 )
			.maxLevelCost( level->level * 12 + 26 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< EnlightenmentEnchantment > {
		final DoubleConfig experienceMultiplier = new DoubleConfig( 0.25, new Range<>( 0.01, 10.0 ) );

		public Modifier() {
			super( Registries.ENLIGHTENMENT, Registries.Modifiers.ENCHANTMENT );

			new OnPickupXp.Context( this::increaseExperience )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addConfig( this.experienceMultiplier.name( "experience_extra_multiplier" )
					.comment( "Extra percent of experience acquired from all sources per enchantment level." )
				).insertTo( this );

			new OnEquipmentChanged.Context( this::giveAdvancement )
				.addCondition( data->data.entity instanceof ServerPlayer )
				.addCondition( data->this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) == 8 )
				.insertTo( this );

			this.name( "Enlightenment" ).comment( "Increases the experience gained from any source." );
		}

		private void increaseExperience( OnPickupXp.Data data ) {
			int enlightenmentSum = this.enchantment.get().getEnchantmentSum( data.player, EquipmentSlots.ARMOR );
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
