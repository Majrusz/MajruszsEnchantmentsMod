package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.gamemodifiers.contexts.OnPickupXp;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class EnlightenmentEnchantment extends CustomEnchantment {
	public EnlightenmentEnchantment() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 12 + 6 )
			.maxLevelCost( level->level * 12 + 26 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig experienceMultiplier = new DoubleConfig( 0.25, new Range<>( 0.01, 10.0 ) );
		final Supplier< EnlightenmentEnchantment > enchantment = Registries.ENLIGHTENMENT;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Enlightenment" )
				.comment( "Increases the experience gained from any source." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnPickupXp.listen( this::increaseExperience )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addConfig( this.experienceMultiplier.name( "experience_extra_multiplier" )
					.comment( "Extra percent of experience acquired from all sources per enchantment level." )
				).insertTo( group );

			OnEquipmentChanged.listen( this::giveAdvancement )
				.addCondition( Condition.predicate( data->data.entity instanceof ServerPlayer ) )
				.addCondition( Condition.predicate( data->this.enchantment.get().getEnchantmentSum( data.entity, EquipmentSlots.ARMOR ) == 8 ) )
				.insertTo( group );
		}

		private void increaseExperience( OnPickupXp.Data data ) {
			int enlightenmentSum = this.enchantment.get().getEnchantmentSum( data.player, EquipmentSlots.ARMOR );
			int experiencePoints = Random.roundRandomly( enlightenmentSum * this.experienceMultiplier.get() * data.event.getOrb().getValue() );
			if( experiencePoints > 0 ) {
				data.player.giveExperiencePoints( experiencePoints );
			}
		}

		private void giveAdvancement( OnEquipmentChanged.Data data ) {
			Registries.HELPER.triggerAchievement( ( ServerPlayer )data.entity, "enlightenment_8" );
		}
	}
}
