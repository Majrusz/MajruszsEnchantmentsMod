package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnExpOrbPickedUp;
import com.mlib.contexts.OnItemEquipped;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class EnlightenmentEnchantment extends Handler {
	float multiplier = 0.125f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.maxLevel( 2 )
			.minLevelCost( level->level * 12 + 6 )
			.maxLevelCost( level->level * 12 + 26 );
	}

	public EnlightenmentEnchantment() {
		super( MajruszsEnchantments.ENLIGHTENMENT, false );

		OnExpOrbPickedUp.listen( this::increaseExperience )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.player ) );

		OnItemEquipped.listen( this::giveAdvancement )
			.addCondition( data->data.entity instanceof ServerPlayer )
			.addCondition( data->EnchantmentHelper.getLevelSum( this.enchantment, data.entity ) >= 8 );

		this.config.defineFloat( "experience_multiplier_per_level", s->this.multiplier, ( s, v )->this.multiplier = Range.of( 0.0f, 10.0f ).clamp( v ) );
	}

	private void increaseExperience( OnExpOrbPickedUp data ) {
		data.experience += Random.round( EnchantmentHelper.getLevelSum( this.enchantment, data.player ) * this.multiplier * data.original );
	}

	private void giveAdvancement( OnItemEquipped data ) {
		MajruszsEnchantments.HELPER.triggerAchievement( ( ServerPlayer )data.entity, "enlightenment_8" );
	}
}
