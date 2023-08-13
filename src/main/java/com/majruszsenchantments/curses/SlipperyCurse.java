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
import com.mlib.contexts.OnPlayerTick;
import com.mlib.math.Range;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;

import java.util.function.Supplier;

public class SlipperyCurse extends CustomEnchantment {
	public SlipperyCurse() {
		this.rarity( Rarity.RARE )
			.category( Registries.TOOLS )
			.slots( EquipmentSlots.BOTH_HANDS )
			.curse()
			.maxLevel( 1 )
			.minLevelCost( level->10 )
			.maxLevelCost( level->50 );
	}

	@AutoInstance
	public static class Handler {
		final Supplier< SlipperyCurse > enchantment = Registries.SLIPPERY;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.CURSE )
				.name( "Slippery" )
				.comment( "Makes the item occasionally drop out of hand." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			DoubleConfig dropCooldown = new DoubleConfig( 1.0, new Range<>( 0.1, 300.0 ) );
			dropCooldown.name( "drop_cooldown" ).comment( "Cooldown in seconds between ticks." );

			DoubleConfig dropChance = new DoubleConfig( 0.03, Range.CHANCE );
			dropChance.name( "drop_chance" ).comment( "Chance to drop held item every tick." );

			OnPlayerTick.listen( this::dropWeapon )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addCondition( Condition.cooldown( dropCooldown, Dist.DEDICATED_SERVER ) )
				.addCondition( Condition.chance( dropChance ) )
				.insertTo( group );
		}

		private void dropWeapon( OnPlayerTick.Data data ) {
			EquipmentSlot slot = this.enchantment.get().hasEnchantment( data.player.getMainHandItem() ) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

			data.player.drop( data.player.getItemBySlot( slot ), false );
			data.player.setItemSlot( slot, ItemStack.EMPTY );
		}
	}
}
