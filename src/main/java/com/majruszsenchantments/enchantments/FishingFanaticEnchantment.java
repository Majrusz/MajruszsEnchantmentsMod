package com.majruszsenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.mlib.EquipmentSlots;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.config.DoubleArrayConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.contexts.OnItemFishedContext;
import com.mlib.gamemodifiers.data.OnItemFishedData;
import com.mlib.math.VectorHelper;
import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Function;
import java.util.function.Supplier;

public class FishingFanaticEnchantment extends CustomEnchantment {
	protected DoubleConfig damageBonus;

	public static Supplier< FishingFanaticEnchantment > create() {
		Parameters params = new Parameters( Rarity.UNCOMMON, EnchantmentCategory.FISHING_ROD, EquipmentSlots.BOTH_HANDS, false, 8, level->10 * level, level->20 + 10 * level );
		FishingFanaticEnchantment enchantment = new FishingFanaticEnchantment( params );
		Modifier modifier = new FishingFanaticEnchantment.Modifier( enchantment );

		return ()->enchantment;
	}

	public FishingFanaticEnchantment( Parameters params ) {
		super( params );
	}

	@Override
	public float getDamageBonus( int level, MobType creature, ItemStack enchantedItem ) {
		return ( float )( level * this.damageBonus.get() );
	}

	@Override
	public Component getFullname( int level ) {
		if( level == this.getMaxLevel() ) {
			return Component.translatable( "majruszsenchantments.true_level" )
				.append( " " )
				.append( Component.translatable( getDescriptionId() ) )
				.withStyle( ChatFormatting.GRAY );
		}

		return super.getFullname( level );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	private static class Modifier extends EnchantmentModifier< FishingFanaticEnchantment > {
		static final ResourceLocation SPECIAL_LOOT_TABLE = Registries.getLocation( "gameplay/fishing/fishing_fanatic_extra" );
		static final Function< Integer, String > LEVEL_FORMAT = idx->String.format( "level_%d", idx + 1 );
		final DoubleArrayConfig levelUpChances = new DoubleArrayConfig( "LevelUpChances", "Chances to acquire enchantment level when an item is fished out.", LEVEL_FORMAT, false, 0.0, 1.0, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01, 0.004, 0.002 );
		final DoubleArrayConfig specialDropChance = new DoubleArrayConfig( "SpecialDropChances", "Chance for each extra item to be replaced with a better one.", LEVEL_FORMAT, false, 0.0, 1.0, 0.00, 0.00, 0.00, 0.00, 0.0, 0.02, 0.04, 0.06 );
		final DoubleConfig extraLootChance = new DoubleConfig( "extra_loot_chance", "Independent chance for extra loot per enchantment level.", false, 0.33333, 0.0, 1.0 );
		final DoubleConfig rainMultiplier = new DoubleConfig( "rain_multiplier", "Multiplier for 'LevelUpChances' when it is raining.", false, 2.0, 1.0, 10.0 );
		final DoubleConfig damageBonus = new DoubleConfig( "damage_bonus", "Amount of extra damage dealt by the fishing rod per enchantment level.", false, 1.0, 0.0, 5.0 );

		public Modifier( FishingFanaticEnchantment enchantment ) {
			super( enchantment, "FishingFanatic", "Gives a chance to catch additional items from fishing." );
			enchantment.damageBonus = this.damageBonus;

			OnItemFishedContext onItemFished = new OnItemFishedContext( this::increaseLoot );
			onItemFished.addCondition( new Condition.IsServer() );

			this.addContext( onItemFished );
			this.addConfigs( this.levelUpChances, this.specialDropChance, this.extraLootChance, this.rainMultiplier, this.damageBonus );
		}

		private void increaseLoot( OnItemFishedData data ) {
			assert data.level != null;
			Multiset< String > rewards = HashMultiset.create();
			rewards.add( data.event.getDrops().get( 0 ).getHoverName().getString() );

			ItemStack fishingRod = this.enchantment.deduceUsedHandItem( data.player );
			int fanaticLevel = this.enchantment.getEnchantmentLevel( fishingRod );
			int rewardsCounter = spawnExtraLoot( data, fanaticLevel, fishingRod, rewards );
			if( tryIncreaseEnchantmentLevel( data, fanaticLevel, fishingRod ) ) {
				sendLevelUpMessage( data.player );
			} else if( rewardsCounter > 0 ) {
				sendRewardsMessage( data.player, rewards );
			}
			if( rewardsCounter > 0 ) {
				data.event.damageRodBy( data.event.getRodDamage() + rewardsCounter );
				data.level.addFreshEntity( new ExperienceOrb( data.level, data.player.getX(), data.player.getY() + 0.5, data.player.getZ() + 0.5, rewardsCounter + Random.nextInt( 1, 2 * rewardsCounter + 1 ) ) );
			}
		}

		private int spawnExtraLoot( OnItemFishedData data, int fanaticLevel, ItemStack fishingRod, Multiset< String > rewards ) {
			assert data.level != null;
			LootContext lootContext = generateLootContext( data.player, fishingRod );
			LootTable standardLootTable = getLootTable( BuiltInLootTables.FISHING );
			LootTable specialLootTable = getLootTable( SPECIAL_LOOT_TABLE );
			FishingHook hook = data.event.getHookEntity();

			int counter = 0;
			for( int i = 0; i < fanaticLevel && this.enchantment.isEnabled(); i++ )
				if( Random.tryChance( this.extraLootChance.get() ) ) {
					LootTable lootTable = Random.tryChance( this.specialDropChance.get( fanaticLevel - 1 ) ) ? specialLootTable : standardLootTable;
					for( ItemStack extraReward : lootTable.getRandomItems( lootContext ) ) {
						Vec3 spawnPosition = hook.position().add( Random.getRandomVector3d( -0.25, 0.25, 0.125, 0.5, -0.25, 0.25 ) );
						ItemEntity itemEntity = new ItemEntity( data.level, spawnPosition.x, spawnPosition.y, spawnPosition.z, extraReward );
						Vec3 motion = data.player.position().subtract( itemEntity.position() ).multiply( 0.1, 0.1, 0.1 );
						itemEntity.setDeltaMovement( motion.add( 0.0, Math.pow( VectorHelper.length( motion ), 0.5 ) * 0.25, 0.0 ) );
						data.level.addFreshEntity( itemEntity );

						rewards.add( extraReward.getHoverName().getString() );
						++counter;
					}
				}

			return counter;
		}

		private static LootContext generateLootContext( Player player, ItemStack fishingRod ) {
			LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerLevel )player.level );
			lootContextBuilder.withParameter( LootContextParams.TOOL, fishingRod )
				.withRandom( MajruszLibrary.RANDOM )
				.withLuck( player.getLuck() )
				.withParameter( LootContextParams.ORIGIN, player.position() );

			return lootContextBuilder.create( LootContextParamSets.FISHING );
		}

		private static LootTable getLootTable( ResourceLocation location ) {
			return ServerLifecycleHooks.getCurrentServer().getLootTables().get( location );
		}

		private boolean tryIncreaseEnchantmentLevel( OnItemFishedData data, int fanaticLevel, ItemStack fishingRod ) {
			assert data.level != null;
			boolean isRaining = data.level.isRaining();
			double rainMultiplier = isRaining ? this.rainMultiplier.get() : 1.0;
			if( fanaticLevel == this.enchantment.getMaxLevel() || !Random.tryChance( this.levelUpChances.get( fanaticLevel ) * rainMultiplier ) ) {
				return false;
			}

			this.enchantment.increaseEnchantmentLevel( fishingRod );
			if( data.player instanceof ServerPlayer player ) {
				giveAdvancement( player, "nothing_can_stop_me", ()->isRaining );
				giveAdvancement( player, "fishing_fanatic", ()->fanaticLevel + 1 == 1 );
				giveAdvancement( player, "fishing_fanatic_true", ()->fanaticLevel + 1 == this.enchantment.getMaxLevel() );
			}
			return true;
		}

		private void giveAdvancement( ServerPlayer player, String type, Supplier< Boolean > check ) {
			if( check.get() ) {
				Registries.BASIC_TRIGGER.trigger( player, type );
			}
		}

		private void sendLevelUpMessage( Player player ) {
			player.displayClientMessage( Component.translatable( "majruszsenchantments.fanatic_level_up" ).withStyle( ChatFormatting.BOLD ), true );
		}

		private void sendRewardsMessage( Player player, Multiset< String > rewards ) {
			MutableComponent message = Component.literal( ChatFormatting.WHITE + "(" );
			ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards ).elementSet().asList();
			for( int i = 0; i < rewardList.size(); i++ ) {
				message.append( Component.literal( ( ( i == 0 ) ? ChatFormatting.WHITE : ChatFormatting.GOLD ) + rewardList.get( i ) ) );
				if( rewards.count( rewardList.get( i ) ) > 1 )
					message.append( Component.literal( ChatFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );
				if( i != rewardList.size() - 1 )
					message.append( Component.literal( ChatFormatting.WHITE + ", " ) );
			}
			message.append( Component.literal( ChatFormatting.WHITE + ")" ) );

			player.displayClientMessage( message, true );
		}
	}
}
