package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.config.ConfigGroup;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.Priority;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.math.AnyPos;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class TelekinesisEnchantment extends CustomEnchantment {
	public TelekinesisEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.TOOLS )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	@AutoInstance
	public static class Handler {
		static final ParticleHandler PARTICLE = new ParticleHandler( Registries.TELEKINESIS_PARTICLE, ParticleHandler.offset( 0.5f ), ParticleHandler.speed( 0.015f ) );
		final BooleanConfig particlesVisibility = new BooleanConfig( true );
		final Supplier< TelekinesisEnchantment > enchantment = Registries.TELEKINESIS;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Telekinesis" )
				.comment( "Adds acquired items directly to player's inventory." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnLoot.listen( data->this.addToInventory( data, data.entity ) )
				.priority( Priority.LOWEST )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.entity instanceof Player ) )
				.addCondition( Condition.predicate( data->data.tool != null && this.enchantment.get().hasEnchantment( data.tool ) ) )
				.addCondition( OnLoot.hasOrigin() )
				.addConfig( this.particlesVisibility.name( "spawn_particles" ).comment( "Determines whether Telekinesis should spawn any particles." ) )
				.insertTo( group );

			OnLoot.listen( data->this.addToInventory( data, data.killer ) )
				.priority( Priority.LOWEST )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->data.killer instanceof Player ) )
				.addCondition( Condition.predicate( data->this.enchantment.get().hasEnchantment( ( Player )data.killer ) ) )
				.addCondition( OnLoot.hasOrigin() )
				.insertTo( group );

			OnLoot.listen( data->this.addToInventory( data, data.killer ) )
				.priority( Priority.LOWEST )
				.addCondition( Condition.isServer() )
				.addCondition( OnLoot.hasOrigin() )
				.addCondition( Condition.predicate( data->data.killer instanceof Player ) )
				.addCondition( Condition.predicate( this::doesProjectileHasEnchantment ) )
				.insertTo( group );
		}

		private void addToInventory( OnLoot.Data data, Entity entity ) {
			Player player = ( Player )entity;
			assert player != null && data.getLevel() != null;
			if( data.generatedLoot.removeIf( player::addItem ) ) {
				SoundHandler.ITEM_PICKUP.play( data.getLevel(), player.position(), SoundHandler.randomized( 0.25f ) );
				if( this.particlesVisibility.isEnabled() ) {
					this.spawnParticles( data, player );
				}
			}
		}

		private void spawnParticles( OnLoot.Data data, Player player ) {
			Vec3 from = AnyPos.from( data.origin ).add( 0.0, data.killer != null && data.entity != null ? data.entity.getBbHeight() * 0.75 : 0.0, 0.0 ).vec3();
			Vec3 to = AnyPos.from( player.position() ).add( 0.0, player.getBbHeight() * 0.5, 0.0 ).vec3();
			PARTICLE.spawnLine( data.getServerLevel(), from, to, 3 );
		}

		private boolean doesProjectileHasEnchantment( OnLoot.Data data ) {
			if( data.damageSource != null ) {
				ItemStack weapon = IMixinProjectile.getWeaponFromDirectEntity( data.damageSource );
				return weapon != null && this.enchantment.get().hasEnchantment( weapon );
			}

			return false;
		}
	}
}
