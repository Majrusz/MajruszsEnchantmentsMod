package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.blocks.BlockHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnFarmlandTillCheck;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import com.mlib.math.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Supplier;

public class HarvesterEnchantment extends CustomEnchantment {
	public static Supplier< HarvesterEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, Registries.HOE, EquipmentSlots.BOTH_HANDS, false, 3, level->10 * level, level->15 + 10 * level );
		HarvesterEnchantment enchantment = new HarvesterEnchantment( params );
		Modifier modifier = new HarvesterEnchantment.Modifier( enchantment );

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

			OnPlayerInteract.Context onInteract = new OnPlayerInteract.Context( this::handle );
			onInteract.addCondition( new Condition.IsServer<>() )
				.addCondition( data->enchantment.hasEnchantment( data.itemStack ) )
				.addCondition( data->data.event instanceof PlayerInteractEvent.RightClickBlock )
				.addCondition( data->BlockHelper.isCropAtMaxAge( data.level, new BlockPos( data.event.getPos() ) ) );

			OnLoot.Context onLoot = new OnLoot.Context( this::replant );
			onLoot.addCondition( new Condition.IsServer<>() )
				.addCondition( OnLoot.HAS_BLOCK_STATE )
				.addCondition( OnLoot.HAS_ENTITY )
				.addCondition( OnLoot.HAS_TOOL )
				.addCondition( OnLoot.HAS_ORIGIN )
				.addCondition( data->BlockHelper.isCropAtMaxAge( data.level, new BlockPos( data.origin ) ) );

			OnFarmlandTillCheck.Context onCheck = new OnFarmlandTillCheck.Context( OnFarmlandTillCheck.INCREASE_AREA );
			onCheck.addCondition( data->enchantment.hasEnchantment( data.itemStack ) );

			this.addConfigs( this.durabilityPenalty, this.growChance );
			this.addContexts( onInteract, onLoot );
		}

		private void handle( OnPlayerInteract.Data data ) {
			assert data.level != null;
			Vec3 position = VectorHelper.vec3( data.event.getPos() );
			collectCrop( data.level, data.player, new BlockPos( position ), data.itemStack );
			tickNearbyCrops( data.level, data.player, new BlockPos( position ), data.itemStack, data.event.getHand() );
			SoundHandler.BONE_MEAL.play( data.level, position );
		}

		private void collectCrop( ServerLevel level, Player player, BlockPos position, ItemStack itemStack ) {
			BlockState blockState = level.getBlockState( position );
			Block block = blockState.getBlock();
			block.playerDestroy( level, player, position, blockState, null, itemStack );
		}

		private void tickNearbyCrops( ServerLevel level, Player player, BlockPos position, ItemStack itemStack,
			InteractionHand hand
		) {
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

					int particlesCount = 1;
					if( Random.tryChance( this.growChance.get() ) ) {
						BlockHelper.growCrop( level, neighbourPosition );
						totalDamage += this.durabilityPenalty.get();
						particlesCount = 3;
					}

					ParticleHandler.AWARD.spawn( level, VectorHelper.vec3( position ), particlesCount );
				}
			}
			int finalDamage = Random.roundRandomly( totalDamage );
			if( finalDamage > 0 ) {
				itemStack.hurtAndBreak( finalDamage, player, owner->owner.broadcastBreakEvent( hand ) );
			}
		}

		private void replant( OnLoot.Data data ) {
			assert data.origin != null && data.blockState != null && data.level != null;
			BlockPos position = new BlockPos( data.origin );
			Block block = data.blockState.getBlock();
			Item seedItem = getSeedItem( data.level, data.blockState, position );
			for( ItemStack itemStack : data.generatedLoot ) {
				if( itemStack.getItem() == seedItem ) {
					itemStack.setCount( itemStack.getCount() - 1 );
					data.level.setBlockAndUpdate( position, block.defaultBlockState() );
					return;
				}
			}

			data.level.setBlockAndUpdate( position, Blocks.AIR.defaultBlockState() );
		}

		private static Item getSeedItem( Level level, BlockState blockState, BlockPos position ) {
			return blockState.getBlock().getCloneItemStack( level, position, blockState ).getItem();
		}
	}
}
