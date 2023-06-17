package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class DodgeEnchantment extends CustomEnchantment {
	public DodgeEnchantment() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR_LEGS )
			.slots( EquipmentSlots.LEGS )
			.maxLevel( 2 )
			.minLevelCost( level->level * 14 )
			.maxLevelCost( level->level * 14 + 20 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig chance = new DoubleConfig( 0.125, new Range<>( 0.01, 0.5 ) );
		final Supplier< DodgeEnchantment > enchantment = Registries.DODGE;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Dodge" )
				.comment( "Gives a chance to completely avoid any kind of damage." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnPreDamaged.listen( this::dodgeDamage )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.target ) )
				.addCondition( OnPreDamaged.dealtAnyDamage() )
				.addCondition( OnPreDamaged.willTakeFullDamage() )
				.addCondition( Condition.predicate( this::tryToDodge ) )
				.addConfig( this.chance.name( "chance" ).comment( "Chance to completely ignore the damage per enchantment level." ) )
				.insertTo( group );
		}

		private void dodgeDamage( OnPreDamaged.Data data ) {
			if( data.getLevel() instanceof ServerLevel level ) {
				this.spawnEffects( data.target, level );
			}

			OnPreDamaged.CANCEL.accept( data );
		}

		private void spawnEffects( LivingEntity entity, ServerLevel level ) {
			for( double d = 0.0; d < 3.0; d++ ) {
				Vec3 position = new Vec3( 0.0, entity.getBbHeight() * 0.25 * ( d + 1.0 ), 0.0 ).add( entity.position() );
				for( int i = 0; i < 2; i++ ) {
					level.sendParticles( Registries.DODGE_PARTICLE.get(), position.x, position.y, position.z, 5 * ( 2 * i + 1 ), ( i + 1 ) * 0.25, 0.375, ( i + 1 ) * 0.25, 0.0075 );
				}
			}
			SoundHandler.FIRE_EXTINGUISH.play( level, entity.position() );
		}

		private boolean tryToDodge( OnPreDamaged.Data data ) {
			return Random.tryChance( this.enchantment.get().getEnchantmentLevel( data.target ) * this.chance.get() );
		}
	}
}
