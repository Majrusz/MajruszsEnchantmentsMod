package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszsenchantments.particles.TelekinesisParticleType;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.contexts.base.Priority;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.emitter.SoundEmitter;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.AnyPos;
import com.mlib.math.Random;
import com.mlib.mixininterfaces.IMixinProjectile;
import com.mlib.time.TimeHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

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

		Vec3 from = AnyPos.from( data.origin ).add( 0.0, data.killer != null && data.entity != null ? data.entity.getBbHeight() * 0.75 : 0.0, 0.0 ).vec3();
		Vec3 to = AnyPos.from( player.position() ).add( 0.0, player.getBbHeight() * 0.5, 0.0 ).vec3();
		AtomicInteger idx = new AtomicInteger();
		double distance = to.distanceTo( from );
		int count = Random.round( to.distanceTo( from ) * 6 );
		int lifetime = TimeHelper.toTicks( 0.5 ) + Random.round( distance * 0.2 );
		float pulseSpeed = ( float )( 1.0f + 0.05f * distance );

		ParticleEmitter.of( ()->new TelekinesisParticleType.Options( ( int )( lifetime * ( count - 1.0f * idx.getAndUpdate( x->x + 1 ) ) / count ), lifetime, pulseSpeed ) )
			.offset( ()->new Vec3( 0.0, 0.0, 0.0 ) )
			.speed( 0.0f )
			.count( count )
			.position( from )
			.emitLine( data.getServerLevel(), to );
	}
}
