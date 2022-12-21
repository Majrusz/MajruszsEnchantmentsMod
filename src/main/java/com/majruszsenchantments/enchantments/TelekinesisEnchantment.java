package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
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

import java.util.function.Predicate;
import java.util.function.Supplier;

public class TelekinesisEnchantment extends CustomEnchantment {
	public static Supplier< TelekinesisEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.TOOLS, EquipmentSlots.MAINHAND, false, 1, level->15, level->45 );
		TelekinesisEnchantment enchantment = new TelekinesisEnchantment( params );
		Modifier modifier = new TelekinesisEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public TelekinesisEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< TelekinesisEnchantment > {
		public Modifier( TelekinesisEnchantment enchantment ) {
			super( enchantment, "Telekinesis", "Adds acquired items directly to player's inventory." );

			OnLoot.Context onLoot = new OnLoot.Context( data->this.addToInventory( data, data.entity ) );
			onLoot.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.entity instanceof Player )
				.addCondition( data->data.tool != null && enchantment.hasEnchantment( data.tool ) );

			OnLoot.Context onLoot2 = new OnLoot.Context( data->this.addToInventory( data, data.killer ) );
			onLoot2.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( data->enchantment.hasEnchantment( ( Player )data.killer ) );

			OnLoot.Context onLoot3 = new OnLoot.Context( data->this.addToInventory( data, data.killer ) );
			onLoot3.priority( Priority.LOWEST )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( this.doesProjectileHasEnchantmentPredicate() );

			this.addContexts( onLoot, onLoot2, onLoot3 );
		}

		private void addToInventory( OnLoot.Data data, Entity entity ) {
			Player player = ( Player )entity;
			assert player != null && data.level != null;
			if( data.generatedLoot.removeIf( player::addItem ) ) {
				SoundHandler.ITEM_PICKUP.play( data.level, player.position(), SoundHandler.randomized( 0.25f ) );
				Vec3 from = data.origin;
				Vec3 to = VectorHelper.add( player.position(), new Vec3( 0.0, player.getBbHeight() * 0.5, 0.0 ) );
				ParticleHandler.WITCH.spawnLine( data.level, from, to, 1, ParticleHandler.offset( 0.05f ) );
			}
		}

		private Predicate< OnLoot.Data > doesProjectileHasEnchantmentPredicate() {
			return data->{
				if( data.damageSource != null ) {
					ItemStack weapon = IMixinProjectile.getWeaponFromDirectEntity( data.damageSource );
					return weapon != null && this.enchantment.hasEnchantment( weapon );
				}

				return false;
			};
		}
	}
}
