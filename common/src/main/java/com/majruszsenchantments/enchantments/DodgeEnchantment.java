package com.majruszsenchantments.enchantments;

import com.majruszlibrary.data.Reader;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.majruszlibrary.MajruszLibrary;
import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.base.Condition;
import com.majruszlibrary.emitter.ParticleEmitter;
import com.majruszlibrary.emitter.SoundEmitter;
import com.majruszlibrary.entity.EntityHelper;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.EnchantmentHelper;
import com.majruszlibrary.item.EquipmentSlots;
import com.majruszlibrary.math.AnyPos;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.time.TimeHelper;
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
		super( MajruszsEnchantments.DODGE, DodgeEnchantment.class, false );

		OnEntityPreDamaged.listen( this::dodge )
			.addCondition( Condition.isLogicalServer() )
			.addCondition( OnEntityPreDamaged::willTakeFullDamage )
			.addCondition( data->data.attacker != null )
			.addCondition( data->Random.check( EnchantmentHelper.getLevel( this.enchantment, data.target ) * this.chance ) );

		this.config.define( "dodge_chance_per_level", Reader.number(), s->this.chance, ( s, v )->this.chance = Range.CHANCE.clamp( v ) );
	}

	private void dodge( OnEntityPreDamaged data ) {
		int invisibleDuration = TimeHelper.toTicks( 1.0 ); // TODO: test on server

		data.cancelDamage();
		this.spawnEffects( data, invisibleDuration );
		MajruszLibrary.ENTITY_INVISIBLE.sendToClients( new EntityHelper.EntityInvisible( data.target, invisibleDuration ) );
	}

	private void spawnEffects( OnEntityPreDamaged data, int invisibleDuration ) {
		TimeHelper.slider( invisibleDuration, slider->{
			boolean isFirstOrLastTick = slider.getTicksLeft() == slider.getTicksTotal() || slider.getTicksLeft() == 0;
			float width = data.target.getBbWidth();
			float height = data.target.getBbHeight();
			int count = 1;

			if( isFirstOrLastTick ) {
				count = 20;
				SoundEmitter.of( SoundEvents.FIRE_EXTINGUISH )
					.position( data.target.position() )
					.volume( SoundEmitter.randomized( 0.25f ) )
					.pitch( SoundEmitter.randomized( 0.4f ) )
					.emit( data.getServerLevel() );
			}

			ParticleEmitter.of( MajruszsEnchantments.DODGE_PARTICLE )
				.sizeBased( data.target )
				.count( count )
				.offset( ()->AnyPos.from( width, height, width ).mul( 1.0f, 0.25f, 1.0f ).vec3() )
				.speed( 0.025f )
				.emit( data.getServerLevel() );
		} );
	}
}
