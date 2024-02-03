package com.majruszsenchantments.curses;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.events.OnEntityTicked;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.level.LevelHelper;
import com.majruszlibrary.math.Range;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.world.damagesource.DamageSource;
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
		super( MajruszsEnchantments.CORROSION, CorrosionCurse.class, true );

		OnEntityTicked.listen( this::dealDamage )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( Condition.cooldown( ()->this.cooldown ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.entity ) )
			.addCondition( data->LevelHelper.isRainingAt( data.getLevel(), data.entity.blockPosition() ) || data.entity.isInWater() );

		this.config.define( "damage_dealt_per_level", Reader.number(), s->this.damage, ( s, v )->this.damage = Range.of( 0.0f, 10.0f ).clamp( v ) )
			.define( "damage_cooldown", Reader.number(), s->this.cooldown, ( s, v )->this.cooldown = Range.of( 0.05f, 60.0f ).clamp( v ) );
	}

	private void dealDamage( OnEntityTicked data ) {
		this.damageOwner( data );
		this.damageArmor( data );
	}

	private void damageOwner( OnEntityTicked data ) {
		int levels = EnchantmentHelper.getLevelSum( this.enchantment, data.entity );
		float damage = levels * this.damage;
		if( damage > 0.0f ) {
			data.entity.hurt( DamageSource.MAGIC, damage );
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
