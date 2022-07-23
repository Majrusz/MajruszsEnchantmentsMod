package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnLootContext;
import com.mlib.gamemodifiers.data.OnLootData;
import com.mlib.gamemodifiers.parameters.ContextParameters;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.mixininterfaces.IMixinProjectile;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
		static final ContextParameters LOWEST_PRIORITY = new ContextParameters( Priority.LOWEST, "", "" );

		public Modifier( TelekinesisEnchantment enchantment ) {
			super( enchantment, "Telekinesis", "Adds acquired items directly to player's inventory." );

			OnLootContext onLoot = new OnLootContext( data->this.addToInventory( data, data.entity ), LOWEST_PRIORITY );
			onLoot.addCondition( new Condition.IsServer() )
				.addCondition( data->data.entity instanceof Player )
				.addCondition( data->data.tool != null && enchantment.hasEnchantment( data.tool ) );

			OnLootContext onLoot2 = new OnLootContext( data->this.addToInventory( data, data.killer ), LOWEST_PRIORITY );
			onLoot2.addCondition( new Condition.IsServer() )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( data->enchantment.hasEnchantment( ( Player )data.killer ) );

			OnLootContext onLoot3 = new OnLootContext( data->this.addToInventory( data, data.killer ), LOWEST_PRIORITY );
			onLoot3.addCondition( new Condition.IsServer() )
				.addCondition( data->data.killer instanceof Player )
				.addCondition( data->data.damageSource != null && enchantment.hasEnchantment( IMixinProjectile.getWeaponFromDirectEntity( data.damageSource ) ) );

			this.addContexts( onLoot, onLoot2, onLoot3 );
		}

		private void addToInventory( OnLootData data, Entity entity ) {
			Player player = ( Player )entity;
			assert player != null && data.level != null;
			if( data.generatedLoot.removeIf( player::addItem ) ) {
				Vec3 position = player.position();
				data.level.playSound( null, position.x, position.y, position.z, SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.5f, 0.8f );
			}
		}
	}
}
