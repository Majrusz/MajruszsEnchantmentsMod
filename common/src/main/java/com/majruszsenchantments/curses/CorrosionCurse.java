package com.majruszsenchantments.curses;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityTicked;
import com.mlib.contexts.base.Condition;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.level.LevelHelper;
import com.mlib.math.Range;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class CorrosionCurse extends Handler {
	float damage = 0.25f;
	float cooldown = 3.0f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	public CorrosionCurse() {
		super( MajruszsEnchantments.CORROSION, true );

		OnEntityTicked.listen( this::dealDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( ()->this.cooldown ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) )
			.addCondition( data->LevelHelper.isRainingAt( data.getLevel(), data.entity.blockPosition() ) || data.entity.isInWater() );

		this.config.defineFloat( "damage_dealt_per_level", s->this.damage, ( s, v )->this.damage = Range.of( 0.0f, 10.0f ).clamp( v ) );
		this.config.defineFloat( "damage_cooldown", s->this.cooldown, ( s, v )->this.cooldown = Range.of( 0.05f, 60.0f ).clamp( v ) );
	}

	private void dealDamage( OnEntityTicked data ) {
		this.damageOwner( data );
		this.damageArmor( data );
	}

	private void damageOwner( OnEntityTicked data ) {
		int levels = EnchantmentHelper.getLevelSum( this.enchantment, data.entity );
		float damage = levels * this.damage;
		if( damage > 0.0f ) {
			data.entity.hurt( data.getLevel().damageSources().magic(), damage );
		}
	}

	private void damageArmor( OnEntityTicked data ) {
		for( EquipmentSlot slot : this.enchantment.get().getSlotItems( data.entity ).keySet() ) {
			ItemStack itemStack = data.entity.getItemBySlot( slot );
			if( EnchantmentHelper.has( this.enchantment, itemStack ) ) {
				itemStack.hurtAndBreak( 1, data.entity, owner->owner.broadcastBreakEvent( slot ) );
			}
		}
	}
}
