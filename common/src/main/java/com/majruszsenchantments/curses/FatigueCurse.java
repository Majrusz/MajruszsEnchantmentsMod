package com.majruszsenchantments.curses;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnBreakSpeedGet;
import com.majruszlibrary.events.OnItemEquipped;
import com.majruszlibrary.events.OnItemSwingDurationGet;
import com.majruszlibrary.events.OnItemUseTicked;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class FatigueCurse extends Handler {
	static final Range< Float > MULTIPLIER = Range.of( 0.0f, 1.0f );
	final AttributeHandler attackSpeed;
	final AttributeHandler movementSpeed;
	float miningMultiplier = 0.8f;
	float attackMultiplier = 0.8f;
	float movingMultiplier = 0.95f;
	float usingMultiplier = 0.8f;
	float swingingMultiplier = 0.8f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.BREAKABLE )
			.slots( EquipmentSlots.ALL )
			.curse()
			.maxLevel( 3 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.compatibility( enchantment->!( enchantment instanceof DiggingEnchantment ) );
	}

	public FatigueCurse() {
		super( MajruszsEnchantments.FATIGUE, FatigueCurse.class, true );

		this.attackSpeed = new AttributeHandler( "%s_attack_speed".formatted( this.enchantment.getId() ), ()->Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );
		this.movementSpeed = new AttributeHandler( "%s_movement_speed".formatted( this.enchantment.getId() ), ()->Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );

		OnBreakSpeedGet.listen( this::reduceMiningSpeed )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.player ) );

		OnItemEquipped.listen( this::reduceAttackSpeed );

		OnItemEquipped.listen( this::reduceMovementSpeed );

		OnItemUseTicked.listen( this::reduceUseSpeed )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) )
			.addCondition( data->Random.check( 1.0f - this.getItemMultiplier( this.usingMultiplier, data.entity ) ) );

		OnItemSwingDurationGet.listen( this::increaseSwingDuration )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) );

		this.config.define( "speed_multiplier_per_level", subconfig->{
			subconfig.define( "mining", Reader.number(), s->this.miningMultiplier, ( s, v )->this.miningMultiplier = MULTIPLIER.clamp( v ) );
			subconfig.define( "attacking", Reader.number(), s->this.attackMultiplier, ( s, v )->this.attackMultiplier = MULTIPLIER.clamp( v ) );
			subconfig.define( "moving", Reader.number(), s->this.movingMultiplier, ( s, v )->this.movingMultiplier = MULTIPLIER.clamp( v ) );
			subconfig.define( "item_using", Reader.number(), s->this.usingMultiplier, ( s, v )->this.usingMultiplier = MULTIPLIER.clamp( v ) );
			subconfig.define( "item_swinging", Reader.number(), s->this.swingingMultiplier, ( s, v )->this.swingingMultiplier = MULTIPLIER.clamp( v ) );
		} );
	}

	private void reduceMiningSpeed( OnBreakSpeedGet data ) {
		data.speed *= Math.max( this.getItemMultiplier( this.miningMultiplier, data.player ), 0.01f );
	}

	private void reduceAttackSpeed( OnItemEquipped data ) {
		attackSpeed.setValue( this.getItemMultiplier( this.attackMultiplier, data.entity ) - 1.0f ).apply( data.entity );
	}

	private void reduceMovementSpeed( OnItemEquipped data ) {
		movementSpeed.setValue( this.getArmorMultiplier( this.movingMultiplier, data.entity ) - 1.0f ).apply( data.entity );
	}

	private void reduceUseSpeed( OnItemUseTicked data ) {
		data.duration += 1;
	}

	private void increaseSwingDuration( OnItemSwingDurationGet data ) {
		data.duration += data.original * ( 1.0f - this.getItemMultiplier( this.swingingMultiplier, data.entity ) );
	}

	private float getItemMultiplier( float multiplier, LivingEntity entity ) {
		return ( float )Math.pow( multiplier, EnchantmentHelper.getLevel( this.enchantment, entity.getMainHandItem() ) );
	}

	private float getArmorMultiplier( float multiplier, LivingEntity entity ) {
		return ( float )Math.pow( multiplier, EnchantmentHelper.getLevelSum( this.enchantment, entity, EquipmentSlots.ARMOR ) );
	}
}
