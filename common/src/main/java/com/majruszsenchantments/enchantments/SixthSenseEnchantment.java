package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.MajruszLibrary;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityNoiseCheck;
import com.mlib.contexts.OnEntityNoiseReceived;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.contexts.OnPlayerTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.emitter.SoundEmitter;
import com.mlib.entity.EntityHelper;
import com.mlib.entity.EntityNoiseListener;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.LootHelper;
import com.mlib.math.Range;
import com.mlib.time.TimeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

@AutoInstance
public class SixthSenseEnchantment extends Handler {
	List< ResourceLocation > chestIds = List.of( BuiltInLootTables.ANCIENT_CITY, BuiltInLootTables.ANCIENT_CITY_ICE_BOX );
	ResourceLocation lootId = MajruszsEnchantments.HELPER.getLocation( "chests/ancient_city_sixth_sense" );
	float glowDuration = 2.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment() {
			@Override
			public boolean isTreasureOnly() {
				return true;
			}

			@Override
			public boolean isTradeable() {
				return false;
			}

			@Override
			public boolean canEnchantUsingEnchantingTable( ItemStack itemStack ) {
				return false;
			}
		}
			.rarity( Enchantment.Rarity.VERY_RARE )
			.category( EnchantmentCategory.ARMOR_HEAD )
			.slots( EquipmentSlots.HEAD )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 );
	}

	public SixthSenseEnchantment() {
		super( MajruszsEnchantments.SIXTH_SENSE, false );

		EntityNoiseListener.add( ServerPlayer.class );

		OnEntityNoiseCheck.listen( OnEntityNoiseCheck::makeAudible )
			.addCondition( data->data.listener instanceof ServerPlayer )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, ( ServerPlayer )data.listener ) )
			.addCondition( Condition.isShiftKeyDown( data->( ServerPlayer )data.listener ) )
			.addCondition( Condition.isOnGround( data->data.listener ) );

		OnEntityNoiseReceived.listen( this::highlight )
			.addCondition( data->data.listener instanceof ServerPlayer )
			.addCondition( data->data.emitter != data.listener )
			.addCondition( data->data.emitter != null );

		OnPlayerTicked.listen( this::playSound )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( 1.25f ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.player ) )
			.addCondition( Condition.isShiftKeyDown( data->data.player ) )
			.addCondition( Condition.isOnGround( data->data.player ) );

		OnLootGenerated.listen( this::addToChest )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( data->this.isEnabled )
			.addCondition( data->data.origin != null )
			.addCondition( data->this.chestIds.contains( data.lootId ) );

		this.config.defineFloat( "glow_duration", s->this.glowDuration, ( s, v )->this.glowDuration = Range.of( 0.5f, 30.0f ).clamp( v ) );
		this.config.defineLocationList( "chest_ids", s->this.chestIds, ( s, v )->this.chestIds = v );
		this.config.defineLocation( "loot_id", s->this.lootId, ( s, v )->this.lootId = v );
	}

	private void highlight( OnEntityNoiseReceived data ) {
		MajruszLibrary.ENTITY_GLOW.sendToClient( ( ServerPlayer )data.listener, new EntityHelper.EntityGlow( data.emitter, TimeHelper.toTicks( this.glowDuration ) ) );
	}

	private void playSound( OnPlayerTicked data ) {
		SoundEmitter.of( SoundEvents.WARDEN_HEARTBEAT )
			.volume( SoundEmitter.randomized( 0.3f ) )
			.position( data.player.position() )
			.emit( data.getServerLevel() );
	}

	private void addToChest( OnLootGenerated data ) {
		LootParams params = new LootParams.Builder( data.getServerLevel() )
			.withParameter( LootContextParams.ORIGIN, data.origin )
			.create( LootContextParamSets.CHEST );

		data.generatedLoot.addAll( LootHelper.getLootTable( this.lootId ).getRandomItems( params ) );
	}
}
