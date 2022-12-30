package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnEntityTick;
import com.mlib.levels.LevelHelper;
import com.mlib.math.Range;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;

public class CorrosionCurse extends CustomEnchantment {
	public CorrosionCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@AutoInstance
	public static class Modifier extends EnchantmentModifier< CorrosionCurse > {
		final DoubleConfig damageAmount = new DoubleConfig( 0.25, new Range<>( 0.0, 10.0 ) );

		public Modifier() {
			super( Registries.CORROSION, Registries.Modifiers.CURSE );

			new OnEntityTick.Context( this::damageOnContactWithWater )
				.addCondition( new Condition.IsServer<>() )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment ) )
				.addCondition( new Condition.Cooldown<>( 3.0, Dist.DEDICATED_SERVER ) )
				.addCondition( data->LevelHelper.isEntityOutsideWhenItIsRaining( data.entity ) || data.entity.isInWater() )
				.addConfig( this.damageAmount.name( "damage_amount" ).comment( "Damage dealt to the player every tick per each enchantment level." ) )
				.insertTo( this );

			this.name( "Corrosion" ).comment( "Gradually destroys the item and inflicts damage to the owner when in water." );
		}

		private void damageOnContactWithWater( OnEntityTick.Data data ) {
			assert data.entity != null;
			attackOwner( data.entity );
			damageArmor( data.entity );
		}

		private void attackOwner( LivingEntity entity ) {
			float damage = this.damageAmount.asFloat();
			if( damage > 0.0f ) {
				entity.hurt( DamageSource.MAGIC, damage * this.enchantment.get().getEnchantmentSum( entity, EquipmentSlots.ARMOR ) );
			}
		}

		private void damageArmor( LivingEntity entity ) {
			for( EquipmentSlot equipmentSlotType : EquipmentSlots.ARMOR ) {
				ItemStack itemStack = entity.getItemBySlot( equipmentSlotType );
				if( this.enchantment.get().hasEnchantment( itemStack ) ) {
					itemStack.hurtAndBreak( 1, entity, owner->owner.broadcastBreakEvent( equipmentSlotType ) );
				}
			}
		}
	}
}
