package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.BooleanConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.math.VectorHelper;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TelekinesisEnchantment extends CustomEnchantment {
	public TelekinesisEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.TOOLS )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< TelekinesisEnchantment > {
		static final ParticleHandler PARTICLE = new ParticleHandler( Registries.TELEKINESIS_PARTICLE, ParticleHandler.offset( 0.5f ), ParticleHandler.speed( 0.015f ) );
		final BooleanConfig particlesVisibility = new BooleanConfig( true );

		public Modifier() {
			super( Registries.TELEKINESIS, Registries.Modifiers.ENCHANTMENT );

			new OnLoot.Context( data->this.addToInventory( data, data.entity ) )
				.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.entity instanceof Player )
				.addCondition( data->data.tool != null && this.enchantment.get().hasEnchantment( data.tool ) )
				.insertTo( this );

			new OnLoot.Context( data->this.addToInventory( data, data.killer ) )
				.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( data->this.enchantment.get().hasEnchantment( ( Player )data.killer ) )
				.insertTo( this );

			new OnLoot.Context( data->this.addToInventory( data, data.killer ) )
				.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( this::doesProjectileHasEnchantment )
				.insertTo( this );

			this.addConfig( this.particlesVisibility.name( "spawn_particles" ).comment( "Determines whether Telekinesis should spawn any particles." ) );
			this.name( "Telekinesis" ).comment( "Adds acquired items directly to player's inventory." );
		}

		private void addToInventory( OnLoot.Data data, Entity entity ) {
			Player player = ( Player )entity;
			assert player != null && data.level != null;
			if( data.generatedLoot.removeIf( player::addItem ) ) {
				SoundHandler.ITEM_PICKUP.play( data.level, player.position(), SoundHandler.randomized( 0.25f ) );
				if( this.particlesVisibility.isEnabled() ) {
					this.spawnParticles( data, player );
				}
			}
		}

		private void spawnParticles( OnLoot.Data data, Player player ) {
			Vec3 from = VectorHelper.add( data.origin, new Vec3( 0.0, data.killer != null && data.entity != null ? data.entity.getBbHeight() * 0.75 : 0.0, 0.0 ) );
			Vec3 to = VectorHelper.add( player.position(), new Vec3( 0.0, player.getBbHeight() * 0.5, 0.0 ) );
			PARTICLE.spawnLine( data.level, from, to, 3 );
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
