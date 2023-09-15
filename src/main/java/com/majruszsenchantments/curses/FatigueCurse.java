package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.modhelper.AutoInstance;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.*;
import com.mlib.math.Range;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class FatigueCurse extends CustomEnchantment {
	public FatigueCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DiggingEnchantment ) && super.checkCompatibility( enchantment );
	}

	@AutoInstance
	public static class Handler {
		static final Range< Double > MULTIPLIER_RANGE = new Range<>( 0.1, 0.99 );
		static final AttributeHandler ATTACK_SPEED_ATTRIBUTE = new AttributeHandler( "3f350b5c-4b00-4fbb-8381-c1af0749f779", "FatigueAttackSpeed", Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
		static final AttributeHandler MOVEMENT_SPEED_ATTRIBUTE = new AttributeHandler( "760f7b82-76c7-4875-821e-ef0579b881e0", "FatigueMovementSpeed", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
		final DoubleConfig miningMultiplier = new DoubleConfig( 0.8, MULTIPLIER_RANGE );
		final DoubleConfig attackMultiplier = new DoubleConfig( 0.8, MULTIPLIER_RANGE );
		final DoubleConfig movementMultiplier = new DoubleConfig( 0.95, MULTIPLIER_RANGE );
		final DoubleConfig drawingMultiplier = new DoubleConfig( 0.8, MULTIPLIER_RANGE );
		final DoubleConfig swingMultiplier = new DoubleConfig( 0.8, MULTIPLIER_RANGE );
		final Supplier< FatigueCurse > enchantment = Registries.FATIGUE;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Fatigue" )
				.comment( "Effectively reduces the speed of everything." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnBreakSpeed.listen( this::reduceMiningSpeed )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addConfig( this.miningMultiplier.name( "mining_multiplier" ).comment( "Mining speed multiplier per each level." ) )
				.insertTo( group );

			OnEquipmentChanged.listen( this::reduceAttackSpeed )
				.addConfig( this.attackMultiplier.name( "attack_multiplier" ).comment( "Attack speed multiplier per each level." ) )
				.insertTo( group );

			OnEquipmentChanged.listen( this::reduceMovementSpeed )
				.addConfig( this.movementMultiplier.name( "movement_multiplier" ).comment( "Movement speed multiplier per each level on armor." ) )
				.insertTo( group );

			OnUseItemTick.listen( this::reduceBowstringSpeed )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addCondition( Condition.predicate( data->BowItem.getPowerForTime( data.itemStack.getUseDuration() - data.duration ) > 0.3f ) ) // first frame takes longer than other frames, and we skip slowing this frame
				.addCondition( Condition.predicate( data->Random.tryChance( 1.0f - this.getItemMultiplier( this.drawingMultiplier, data.entity ) ) ) )
				.addConfig( this.drawingMultiplier.name( "drawing_multiplier" ).comment( "Bowstring speed multiplier per each level." ) )
				.insertTo( group );

			OnItemSwingDuration.listen( this::increaseSwingDuration )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addConfig( this.swingMultiplier.name( "swing_multiplier" ).comment( "Swing speed multiplier per each level." ) )
				.insertTo( group );
		}

		private void reduceMiningSpeed( OnBreakSpeed.Data data ) {
			data.newSpeed *= this.getItemMultiplier( this.miningMultiplier, data.player );
		}

		private void reduceAttackSpeed( OnEquipmentChanged.Data data ) {
			ATTACK_SPEED_ATTRIBUTE.setValue( this.getItemMultiplier( this.attackMultiplier, data.entity ) - 1.0f ).apply( data.entity );
		}

		private void reduceMovementSpeed( OnEquipmentChanged.Data data ) {
			MOVEMENT_SPEED_ATTRIBUTE.setValue( getArmorMultiplier( this.movementMultiplier, data.entity ) - 1.0f ).apply( data.entity );
		}

		private void reduceBowstringSpeed( OnUseItemTick.Data data ) {
			data.event.setDuration( data.duration + 1 );
		}

		private void increaseSwingDuration( OnItemSwingDuration.Data data ) {
			data.extraDuration += data.swingDuration / Math.min( 1.0f, getItemMultiplier( this.swingMultiplier, data.entity ) ) - data.swingDuration;
		}

		private float getItemMultiplier( DoubleConfig config, LivingEntity entity ) {
			return ( float )Math.pow( config.get(), this.enchantment.get().getEnchantmentLevel( entity.getMainHandItem() ) );
		}

		private float getArmorMultiplier( DoubleConfig config, LivingEntity entity ) {
			return ( float )Math.pow( config.get(), this.enchantment.get().getEnchantmentSum( entity, EquipmentSlots.ARMOR ) );
		}
	}
}
