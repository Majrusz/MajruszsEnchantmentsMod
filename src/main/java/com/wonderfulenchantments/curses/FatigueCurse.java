package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.contexts.OnBreakSpeedContext;
import com.mlib.gamemodifiers.contexts.OnEquipmentChangedContext;
import com.mlib.gamemodifiers.contexts.OnUseItemTickContext;
import com.mlib.gamemodifiers.data.OnBreakSpeedData;
import com.mlib.gamemodifiers.data.OnEquipmentChangedData;
import com.mlib.gamemodifiers.data.OnUseItemTickData;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class FatigueCurse extends CustomEnchantment {
	public static Supplier< FatigueCurse > create() {
		CustomEnchantment.Parameters params = new CustomEnchantment.Parameters( Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlots.MAINHAND, true, 3, level->10, level->50 );
		FatigueCurse enchantment = new FatigueCurse( params );
		FatigueCurse.Modifier modifier = new FatigueCurse.Modifier( enchantment );

		return ()->enchantment;
	}

	public FatigueCurse( Parameters params ) {
		super( params );
	}

	@Override
	public boolean checkCompatibility( Enchantment enchantment ) {
		return !( enchantment instanceof DiggingEnchantment ) && super.checkCompatibility( enchantment );
	}

	private static class Modifier extends EnchantmentModifier< FatigueCurse > {
		static final double MIN_MULTIPLIER = 0.1, MAX_MULTIPLIER = 0.99;
		static final AttributeHandler ATTACK_SPEED_ATTRIBUTE = new AttributeHandler( "3f350b5c-4b00-4fbb-8381-c1af0749f779", "FatigueAttackSpeed",
			Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL
		);
		static final AttributeHandler MOVEMENT_SPEED_ATTRIBUTE = new AttributeHandler( "760f7b82-76c7-4875-821e-ef0579b881e0", "FatigueMovementSpeed",
			Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL
		);
		final DoubleConfig miningMultiplier = new DoubleConfig( "mining_multiplier", "Mining speed multiplier per each level.", false, 0.8, MIN_MULTIPLIER, MAX_MULTIPLIER );
		final DoubleConfig attackMultiplier = new DoubleConfig( "attack_multiplier", "Attack speed multiplier per each level.", false, 0.8, MIN_MULTIPLIER, MAX_MULTIPLIER );
		final DoubleConfig movementMultiplier = new DoubleConfig( "movement_multiplier", "Movement speed multiplier per each level on armor.", false, 0.95, MIN_MULTIPLIER, MAX_MULTIPLIER );
		final DoubleConfig drawingMultiplier = new DoubleConfig( "drawing_multiplier", "Bowstring speed multiplier per each level.", false, 0.8, MIN_MULTIPLIER, MAX_MULTIPLIER );

		public Modifier( FatigueCurse enchantment ) {
			super( enchantment, "Fatigue", "Reduces mining speed." );

			OnBreakSpeedContext onBreakSpeed = new OnBreakSpeedContext( this::reduceMiningSpeed );
			onBreakSpeed.addCondition( data -> enchantment.hasEnchantment( data.player ) );

			OnEquipmentChangedContext onEquipmentChange = new OnEquipmentChangedContext( this::reduceAttackSpeed );

			OnEquipmentChangedContext onEquipmentChange2 = new OnEquipmentChangedContext( this::reduceMovementSpeed );

			OnUseItemTickContext onBowstring = new OnUseItemTickContext( this::reduceBowstringSpeed );
			onBowstring.addCondition( data -> enchantment.hasEnchantment( data.entity ) )
				.addCondition( data -> Random.tryChance( 1.0f - this.getItemMultiplier( this.drawingMultiplier, data.entity ) ) );

			this.addConfigs( this.miningMultiplier, this.attackMultiplier, this.drawingMultiplier, this.movementMultiplier );
			this.addContexts( onBreakSpeed, onEquipmentChange, onEquipmentChange2, onBowstring );
		}

		private void reduceMiningSpeed( OnBreakSpeedData data ) {
			data.event.setNewSpeed( data.event.getNewSpeed() * getItemMultiplier( this.miningMultiplier, data.player ) );
		}

		private void reduceAttackSpeed( OnEquipmentChangedData data ) {
			ATTACK_SPEED_ATTRIBUTE.setValueAndApply( data.entity, getItemMultiplier( this.attackMultiplier, data.entity ) - 1.0f );
		}

		private void reduceMovementSpeed( OnEquipmentChangedData data ) {
			MOVEMENT_SPEED_ATTRIBUTE.setValueAndApply( data.entity, getArmorMultiplier( this.movementMultiplier, data.entity ) - 1.0f );
		}

		private void reduceBowstringSpeed( OnUseItemTickData data ) {
			data.event.setDuration( data.duration + 1 );
		}

		private float getItemMultiplier( DoubleConfig config, LivingEntity entity ) {
			return ( float )Math.pow( config.get(), this.enchantment.getEnchantmentSum( entity, EquipmentSlots.BOTH_HANDS ) );
		}

		private float getArmorMultiplier( DoubleConfig config, LivingEntity entity ) {
			return ( float )Math.pow( config.get(), this.enchantment.getEnchantmentSum( entity, EquipmentSlots.ARMOR ) );
		}
	}
}
