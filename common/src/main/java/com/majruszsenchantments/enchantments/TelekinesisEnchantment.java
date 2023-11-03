package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.contexts.base.Priority;
import com.mlib.emitter.SoundEmitter;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class TelekinesisEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( MajruszsEnchantments.IS_TOOL )
			.slots( EquipmentSlots.HANDS )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	public TelekinesisEnchantment() {
		super( MajruszsEnchantments.TELEKINESIS, false );

		OnLootGenerated.listen( data->this.addToInventory( data, ( Player )data.entity ) )
			.priority( Priority.LOWEST )
			.addCondition( data->data.entity instanceof Player )
			.addCondition( data->data.origin != null )
			.addCondition( data->data.tool != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.tool ) );

		OnLootGenerated.listen( data->this.addToInventory( data, ( Player )data.killer ) )
			.priority( Priority.LOWEST )
			.addCondition( data->data.killer instanceof Player )
			.addCondition( data->data.origin != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, ( Player )data.killer ) );

		OnLootGenerated.listen( data->this.addToInventory( data, ( Player )data.killer ) )
			.priority( Priority.LOWEST )
			.addCondition( data->data.killer instanceof Player )
			.addCondition( data->data.origin != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, IMixinProjectile.mlib$getProjectileWeapon( data.damageSource.getDirectEntity() ) ) );
	}

	private void addToInventory( OnLootGenerated data, Player player ) {
		if( !data.generatedLoot.removeIf( player::addItem ) ) {
			return;
		}

		SoundEmitter.of( SoundEvents.ITEM_PICKUP )
			.volume( SoundEmitter.randomized( 0.25f ) )
			.position( player.position() )
			.emit( data.getServerLevel() );

		// TODO: particles
	}
}
