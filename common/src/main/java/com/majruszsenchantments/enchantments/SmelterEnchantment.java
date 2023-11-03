package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnLootGenerated;
import com.mlib.contexts.base.Priority;
import com.mlib.entity.EntityHelper;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.item.ItemHelper;
import com.mlib.math.Random;
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
		super( MajruszsEnchantments.SMELTER, false );

		OnLootGenerated.listen( this::smelt )
			.priority( Priority.LOW )
			.addCondition( data->data.blockState != null )
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

		// TODO: particles
	}
}
