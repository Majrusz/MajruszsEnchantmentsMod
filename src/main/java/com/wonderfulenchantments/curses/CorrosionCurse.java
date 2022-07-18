package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntityTickContext;
import com.mlib.gamemodifiers.data.OnEntityTickData;
import com.mlib.levels.LevelHelper;
import com.mlib.time.TimeHelper;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class CorrosionCurse extends CustomEnchantment {
	public static Supplier< CorrosionCurse > create() {
		Parameters params = new Parameters( Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, true, 1, level->10, level->50 );
		CorrosionCurse enchantment = new CorrosionCurse( params );
		Modifier modifier = new CorrosionCurse.Modifier( enchantment );

		return ()->enchantment;
	}

	public CorrosionCurse( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< CorrosionCurse > {
		final DoubleConfig damageCooldown = new DoubleConfig( "damage_cooldown", "Duration in seconds between 'damaging' ticks.", false, 3.0, 1.0, 60.0 );
		final DoubleConfig damageAmount = new DoubleConfig( "damage_amount", "Damage dealt to the player every tick per each enchantment level.", false, 0.25, 0.0, 10.0 );

		public Modifier( CorrosionCurse enchantment ) {
			super( enchantment, "Corrosion", "Gradually destroys the item and inflicts damage to the owner when in water." );

			OnEntityTickContext onTick = new OnEntityTickContext( this::damageOnContactWithWater );
			onTick.addCondition( new Condition.IsServer() )
				.addCondition( data->enchantment.hasEnchantment( data.entity ) )
				.addCondition( data->TimeHelper.hasServerSecondsPassed( this.damageCooldown.get() ) )
				.addCondition( data->LevelHelper.isEntityOutsideWhenItIsRaining( data.entity ) || data.entity.isInWater() );

			this.addConfigs( this.damageCooldown, this.damageAmount );
			this.addContext( onTick );
		}

		private void damageOnContactWithWater( OnEntityTickData data ) {
			assert data.entity != null;
			attackOwner( data.entity );
			damageArmor( data.entity );
		}

		private void attackOwner( LivingEntity entity ) {
			float damage = this.damageAmount.asFloat();
			if( damage > 0.0f ) {
				entity.hurt( DamageSource.DROWN, damage * this.enchantment.getEnchantmentSum( entity, EquipmentSlots.ARMOR ) );
			}
		}

		private void damageArmor( LivingEntity entity ) {
			for( EquipmentSlot equipmentSlotType : EquipmentSlots.ARMOR ) {
				ItemStack itemStack = entity.getItemBySlot( equipmentSlotType );
				if( this.enchantment.hasEnchantment( itemStack ) ) {
					itemStack.hurtAndBreak( 1, entity, owner->owner.broadcastBreakEvent( equipmentSlotType ) );
				}
			}
		}
	}
}
