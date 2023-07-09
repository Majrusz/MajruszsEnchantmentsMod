package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.blocks.BlockHelper;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.effects.ParticleHandler;
import com.mlib.effects.SoundHandler;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.ModConfigs;
import com.mlib.gamemodifiers.contexts.OnEnchantmentAvailabilityCheck;
import com.mlib.gamemodifiers.contexts.OnFarmlandTillCheck;
import com.mlib.gamemodifiers.contexts.OnLoot;
import com.mlib.gamemodifiers.contexts.OnPlayerInteract;
import com.mlib.math.AnyPos;
import com.mlib.math.Range;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Supplier;

public class HarvesterEnchantment extends CustomEnchantment {
	public HarvesterEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( Registries.HOE )
			.slots( EquipmentSlots.BOTH_HANDS )
			.maxLevel( 3 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 15 );
	}

	@AutoInstance
	public static class Handler {
		final DoubleConfig growChance = new DoubleConfig( 0.04, Range.CHANCE );
		final Supplier< HarvesterEnchantment > enchantment = Registries.HARVESTER;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "Harvester" )
				.comment( "Gives the option of right-click harvesting and the chance to grow nearby crops." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnPlayerInteract.listen( this::increaseAgeOfNearbyCrops )
				.addCondition( Condition.isServer() )
				.addCondition( Condition.predicate( data->this.enchantment.get().hasEnchantment( data.itemStack ) ) )
				.addCondition( Condition.predicate( data->data.event instanceof PlayerInteractEvent.RightClickBlock ) )
				.addCondition( Condition.predicate( data->BlockHelper.isCropAtMaxAge( data.getLevel(), new BlockPos( data.event.getPos() ) ) ) )
				.addConfig( this.growChance.name( "extra_grow_chance" ).comment( "Chance to increase an age of nearby crops." ) )
				.insertTo( group );

			OnLoot.listen( this::replant )
				.addCondition( Condition.isServer() )
				.addCondition( OnLoot.hasBlockState() )
				.addCondition( OnLoot.hasEntity() )
				.addCondition( OnLoot.hasTool() )
				.addCondition( OnLoot.hasOrigin() )
				.addCondition( Condition.predicate( data->BlockHelper.isCropAtMaxAge( data.getLevel(), BlockPos.containing( data.origin ) ) ) )
				.insertTo( group );

			OnFarmlandTillCheck.listen( OnFarmlandTillCheck.INCREASE_AREA )
				.addCondition( Condition.predicate( data->this.enchantment.get().hasEnchantment( data.itemStack ) ) )
				.insertTo( group );
		}

		private void increaseAgeOfNearbyCrops( OnPlayerInteract.Data data ) {
			this.collectCrop( data.getServerLevel(), data.player, data.position, data.itemStack );
			this.tickNearbyCrops( data.getServerLevel(), data.position, data.itemStack );
			this.damageHoe( data.itemStack, data.player, data.hand );
			SoundHandler.BONE_MEAL.play( data.getLevel(), AnyPos.from( data.position ).vec3() );
		}

		private void collectCrop( ServerLevel level, Player player, BlockPos position, ItemStack itemStack ) {
			BlockState blockState = level.getBlockState( position );
			Block block = blockState.getBlock();
			block.playerDestroy( level, player, position, blockState, null, itemStack );
		}

		private void tickNearbyCrops( ServerLevel level, BlockPos position, ItemStack itemStack ) {
			int range = this.enchantment.get().getEnchantmentLevel( itemStack );
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
						particlesCount = 3;
					}

					ParticleHandler.AWARD.spawn( level, AnyPos.from( position ).vec3(), particlesCount );
				}
			}
		}

		private void damageHoe( ItemStack itemStack, Player player, InteractionHand hand ) {
			player.swing( hand, true );
			itemStack.hurtAndBreak( 1, player, owner->owner.broadcastBreakEvent( hand ) );
		}

		private void replant( OnLoot.Data data ) {
			assert data.origin != null && data.blockState != null && data.getLevel() != null;
			BlockPos position = BlockPos.containing( data.origin );
			Block block = data.blockState.getBlock();
			Item seedItem = getSeedItem( data.getLevel(), data.blockState, position );
			for( ItemStack itemStack : data.generatedLoot ) {
				if( itemStack.getItem() == seedItem ) {
					itemStack.setCount( itemStack.getCount() - 1 );
					data.getLevel().setBlockAndUpdate( position, block.defaultBlockState() );
					return;
				}
			}

			data.getLevel().setBlockAndUpdate( position, Blocks.AIR.defaultBlockState() );
		}

		private static Item getSeedItem( Level level, BlockState blockState, BlockPos position ) {
			return blockState.getBlock().getCloneItemStack( level, position, blockState ).getItem();
		}
	}
}
