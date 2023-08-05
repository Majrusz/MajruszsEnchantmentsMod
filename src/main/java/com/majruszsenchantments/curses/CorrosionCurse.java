package com.majruszsenchantments.curses;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.contexts.OnEntityTick;
import com.mlib.levels.LevelHelper;
import com.mlib.math.Range;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;

import java.util.function.Supplier;

public class CorrosionCurse extends CustomEnchantment {
	public CorrosionCurse() {
		this.rarity( Rarity.RARE )
			.category( EnchantmentCategory.ARMOR )
			.slots( EquipmentSlots.ARMOR )
			.curse()
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig damageAmount = new DoubleConfig( 0.25, new Range<>( 0.0, 10.0 ) );
		final Supplier< CorrosionCurse > enchantment = Registries.CORROSION;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Corrosion" )
				.comment( "Gradually destroys the item and inflicts damage to the owner when in water." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnEntityTick.listen( this::damageOnContactWithWater )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.entity ) )
				.addCondition( Condition.cooldown( 3.0, Dist.DEDICATED_SERVER ) )
				.addCondition( Condition.predicate( data->LevelHelper.isRainingAt( data.entity ) || data.entity.isInWater() ) )
				.addConfig( this.damageAmount.name( "damage_amount" ).comment( "Damage dealt to the player every tick per each enchantment level." ) )
				.insertTo( group );
		}

		private void damageOnContactWithWater( OnEntityTick.Data data ) {
			assert data.entity != null;
			attackOwner( data.entity );
			damageArmor( data.entity );
		}

		private void attackOwner( LivingEntity entity ) {
			float damage = this.damageAmount.asFloat();
			if( damage > 0.0f ) {
				entity.hurt( entity.level().damageSources().magic(), damage * this.enchantment.get().getEnchantmentSum( entity, EquipmentSlots.ARMOR ) );
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
