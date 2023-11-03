package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.contexts.base.Condition;
import com.mlib.emitter.ParticleEmitter;
import com.mlib.emitter.SoundEmitter;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.AnyPos;
import com.mlib.math.Random;
import com.mlib.math.Range;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

@AutoInstance
public class DodgeEnchantment extends Handler {
	float chance = 0.125f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( EnchantmentCategory.ARMOR_LEGS )
			.slots( EquipmentSlots.LEGS )
			.maxLevel( 2 )
			.minLevelCost( level->level * 14 )
			.maxLevelCost( level->level * 14 + 20 );
	}

	public DodgeEnchantment() {
		super( MajruszsEnchantments.DODGE, false );

		OnEntityPreDamaged.listen( this::dodge )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( OnEntityPreDamaged::willTakeFullDamage )
			.addCondition( data->data.attacker != null )
			.addCondition( data->Random.check( EnchantmentHelper.getLevel( this.enchantment, data.target ) * this.chance ) );

		this.config.defineFloat( "dodge_chance_per_level", ()->this.chance, x->this.chance = Range.CHANCE.clamp( x ) );
	}

	private void dodge( OnEntityPreDamaged data ) {
		data.cancelDamage();

		ParticleEmitter.of( MajruszsEnchantments.DODGE_PARTICLE )
			.sizeBased( data.target )
			.count( 40 )
			.offset( ()->AnyPos.from( data.target.getBbWidth(), data.target.getBbHeight(), data.target.getBbWidth() ).mul( 0.35f, 0.25f, 0.35f ).vec3() )
			.speed( 0.1f )
			.emit( data.getServerLevel() );

		SoundEmitter.of( SoundEvents.FIRE_EXTINGUISH )
			.position( data.target.position() )
			.volume( SoundEmitter.randomized( 0.4f ) )
			.emit( data.getServerLevel() );
	}
}
