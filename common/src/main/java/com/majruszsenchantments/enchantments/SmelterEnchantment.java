package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnLootGenerated;
import com.majruszlibrary.events.base.Priority;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.UntouchingEnchantment;

import java.util.Optional;

@AutoInstance
public class SmelterEnchantment extends Handler {
	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( EnchantmentCategory.DIGGER )
			.slots( EquipmentSlots.MAINHAND )
			.minLevelCost( level->15 )
			.maxLevelCost( level->45 )
			.compatibility( enchantment->!( enchantment instanceof UntouchingEnchantment ) );
	}

	public SmelterEnchantment() {
		super( MajruszsEnchantments.SMELTER, SmelterEnchantment.class, false );

		OnLootGenerated.listen( this::smelt )
			.priority( Priority.LOW )
			.addCondition( data->data.blockState != null )
			.addCondition( data->data.origin != null )
			.addCondition( data->data.tool != null )
			.addCondition( data->data.entity instanceof Player player && !player.isCrouching() )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.tool ) );
	}

	private void smelt( OnLootGenerated data ) {
		float experience = 0.0f;
		for( int idx = 0; idx < data.generatedLoot.size(); ++idx ) {
			Optional< ItemHelper.SmeltResult > result = ItemHelper.tryToSmelt( data.getLevel(), data.generatedLoot.get( idx ) );
			if( result.isPresent() ) {
				experience += result.get().experience();
				data.generatedLoot.set( idx, result.get().itemStack() );
			}
		}

		int totalExperience = Random.round( experience );
		if( totalExperience > 0 ) {
			EntityHelper.spawnExperience( data.getLevel(), data.origin, totalExperience );
		}

		if( experience > 0.0f ) {
			ParticleEmitter.of( MajruszsEnchantments.SMELTER_PARTICLE )
				.count( 10 )
				.offset( ParticleEmitter.offset( 0.2f ) )
				.speed( 0.01f )
				.position( AnyPos.from( data.origin ).center().vec3() )
				.emit( data.getServerLevel() );
		}
	}
}
