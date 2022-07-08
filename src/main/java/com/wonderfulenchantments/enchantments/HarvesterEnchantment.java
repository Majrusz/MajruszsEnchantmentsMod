package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.blocks.BlockHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.features.FarmlandTiller;
import com.mlib.gamemodifiers.contexts.OnPlayerInteractContext;
import com.mlib.gamemodifiers.data.OnPlayerInteractData;
import com.wonderfulenchantments.Registries;
import com.wonderfulenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Supplier;

public class HarvesterEnchantment extends CustomEnchantment {
	public static Supplier< HarvesterEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.HOE, EquipmentSlots.BOTH_HANDS, false, 3, level->10 * level, level->15 + 10 * level );
		HarvesterEnchantment enchantment = new HarvesterEnchantment( params );
		Modifier modifier = new HarvesterEnchantment.Modifier( enchantment );
		FarmlandTiller.addRegister( ( level, player, itemStack )->enchantment.hasEnchantment( itemStack ) );

		return ()->enchantment;
	}

	public HarvesterEnchantment( Parameters params ) {
		super( params );
	}

	private static class Modifier extends EnchantmentModifier< HarvesterEnchantment > {
		final DoubleConfig durabilityPenalty = new DoubleConfig( "durability_penalty", "Durability penalty per each successful increase of nearby crops.", false, 1.0, 0.0, 10.0 );
		final DoubleConfig growChance = new DoubleConfig( "extra_grow_chance", "Chance to increase an age of nearby crops.", false, 0.04, 0.0, 1.0 );

		public Modifier( HarvesterEnchantment enchantment ) {
			super( enchantment, "Harvester", "Gives the option of right-click harvesting and the chance to grow nearby crops." );

			OnPlayerInteractContext onInteract = new OnPlayerInteractContext( this::handle );
			onInteract.addCondition( data->data.level != null )
				.addCondition( data->enchantment.hasEnchantment( data.itemStack ) )
				.addCondition( data->data.event instanceof PlayerInteractEvent.RightClickBlock );

			this.addConfigs( this.durabilityPenalty, this.growChance );
			this.addContext( onInteract );
		}

		private void handle( OnPlayerInteractData data ) {
			assert data.level != null;
			BlockPos position = data.event.getPos();
			if( BlockHelper.isCropAtMaxAge( data.level, position ) ) {
				collectCrop( data.level, data.player, position, data.itemStack );
				tickNearbyCrops( data.level, data.player, position, data.itemStack, data.event.getHand() );
				playSound( data.level, position );
			}
		}

		private void collectCrop( ServerLevel level, Player player, BlockPos position, ItemStack itemStack ) {
			BlockState blockState = level.getBlockState( position );
			Block block = blockState.getBlock();
			block.playerDestroy( level, player, position, blockState, null, itemStack );
		}

		private void tickNearbyCrops( ServerLevel level, Player player, BlockPos position, ItemStack itemStack, InteractionHand hand ) {
			int range = this.enchantment.getEnchantmentLevel( itemStack );
			double totalDamage = 0;
			for( int z = -range; z <= range; ++z ) {
				for( int x = -range; x <= range; ++x ) {
					if( x == 0 && z == 0 )
						continue;

					BlockPos neighbourPosition = position.offset( x, 0, z );
					BlockState blockState = level.getBlockState( position.offset( x, 0, z ) );
					Block block = blockState.getBlock();
					if( !( block instanceof CropBlock ) && !( block instanceof NetherWartBlock ) )
						continue;

					if( Random.tryChance( this.growChance.get() ) ) {
						BlockHelper.growCrop( level, neighbourPosition );
						totalDamage += this.durabilityPenalty.get();
						spawnParticles( level, neighbourPosition, 3 );
					} else {
						spawnParticles( level, neighbourPosition, 1 );
					}
				}
			}
			int finalDamage = Random.roundRandomly( totalDamage );
			if( finalDamage > 0 ) {
				itemStack.hurtAndBreak( finalDamage, player, owner->owner.broadcastBreakEvent( hand ) );
			}
		}

		private void playSound( ServerLevel level, BlockPos position ) {
			level.playSound( null, position, SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 0.25f, 0.5f );
		}

		private void spawnParticles( ServerLevel level, BlockPos position, int count ) {
			level.sendParticles( ParticleTypes.HAPPY_VILLAGER, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, count, 0.25, 0.25, 0.25, 0.1 );
		}
	}
}
