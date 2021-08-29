package com.wonderfulenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.mlib.CommonHelper;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.mlib.math.VectorHelper;
import com.mlib.triggers.BasicTrigger;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

/** Enchantment that increases quality and quantity of loot gathered from fishing. */
@Mod.EventBusSubscriber
public class FanaticEnchantment extends WonderfulEnchantment {
	private static final ResourceLocation SPECIAL_LOOT_TABLE = WonderfulEnchantments.getLocation( "gameplay/fishing/fishing_fanatic_extra" );
	protected final DoubleConfig levelIncreaseChanceMultiplier;
	protected final DoubleConfig highLevelIncreaseChanceMultiplier;
	protected final DoubleConfig extraLootChance;
	protected final DoubleConfig rainingMultiplier;
	protected final DoubleConfig damageBonus;
	protected final IntegerConfig specialDropMinimumLevel;
	protected final DoubleConfig specialDropChance;

	public FanaticEnchantment() {
		super( "fishing_fanatic", Rarity.UNCOMMON, EnchantmentCategory.FISHING_ROD, EquipmentSlot.MAINHAND, "FishingFanatic" );

		String increaseComment = "Chance for increasing enchantment level per every missing level to 6th level. (for example if this value is equal 0.01 then to get 1st level you have 6 * 0.01 = 6% chance, to get 2nd level ( 6-1 ) * 0.01 = 5% chance)";
		this.levelIncreaseChanceMultiplier = new DoubleConfig( "level_increase_chance", increaseComment, false, 0.01, 0.0001, 1.0 );

		String highIncreaseComment = "Chance for increasing enchantment level per every missing level from 6th to 8th level. (for example if this value is equal 0.002 then to get 7th level you have 2 * 0.002 = 0.4% chance and to get 8th level 1 * 0.002 = 0.2% chance)";
		this.highLevelIncreaseChanceMultiplier = new DoubleConfig( "high_level_increase_chance", highIncreaseComment, false, 0.002, 0.0001, 1.0 );

		String lootComment = "Independent chance for extra loot with every enchantment level.";
		this.extraLootChance = new DoubleConfig( "extra_loot_chance", lootComment, false, 0.33333, 0.01, 1.0 );

		String rainingComment = "Chance multiplier when player is fishing while it is raining.";
		this.rainingMultiplier = new DoubleConfig( "raining_multiplier", rainingComment, false, 2.0, 1.0, 10.0 );

		String damageComment = "Amount of extra damage dealt by the fishing rod for every enchantment level.";
		this.damageBonus = new DoubleConfig( "damage_bonus", damageComment, false, 1.0, 0.0, 5.0 );

		String minimumComment = "Minimum required level of Fishing Fanatic to have a chance to drop special items.";
		this.specialDropMinimumLevel = new IntegerConfig( "special_minimum_level", minimumComment, false, 7, 1, 8 );

		String specialComment = "Chance to drop special items instead of regular one. (chance is separate for each item)";
		this.specialDropChance = new DoubleConfig( "special_drop_chance", specialComment, false, 0.05, 0.0, 1.0 );

		this.enchantmentGroup.addConfigs( this.levelIncreaseChanceMultiplier, this.highLevelIncreaseChanceMultiplier, this.extraLootChance,
			this.rainingMultiplier, this.damageBonus, this.specialDropMinimumLevel, this.specialDropChance
		);

		setMaximumEnchantmentLevel( 8 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 10 * level ) );
	}

	@Override
	public float getDamageBonus( int level, MobType creature ) {
		return ( float )( level * this.damageBonus.get() );
	}

	/** Method that displays enchantment name. It is overridden because at maximum level the enchantment will change its name. */
	@Override
	public Component getFullname( int level ) {
		if( level == this.getMaxLevel() ) {
			MutableComponent output = new TranslatableComponent( "wonderful_enchantments.true_level" );

			return output.append( " " )
				.append( new TranslatableComponent( getDescriptionId() ) )
				.withStyle( ChatFormatting.GRAY );
		}

		return super.getFullname( level );
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@SubscribeEvent
	public static void onFishedItem( ItemFishedEvent event ) {
		Player player = event.getPlayer();
		Level level = player.level;
		FanaticEnchantment enchantment = Instances.FISHING_FANATIC;
		LootContext lootContext = generateLootContext( player );
		LootTable standardLootTable = getFishingLootTable(), specialLootTable = getLootTable( SPECIAL_LOOT_TABLE );
		int fanaticLevel = EnchantmentHelper.getEnchantmentLevel( enchantment, player );

		Multiset< String > rewards = HashMultiset.create();
		rewards.add( event.getDrops()
			.get( 0 )
			.getDisplayName()
			.getString() );

		int extraRewardsCounter = 0;
		for( int i = 0; i < fanaticLevel && enchantment.availabilityConfig.isEnabled(); i++ )
			if( Random.tryChance( enchantment.extraLootChance.get() ) ) {
				LootTable lootTable = enchantment.shouldDropSpecialLoot( fanaticLevel ) ? specialLootTable : standardLootTable;
				for( ItemStack extraReward : lootTable.getRandomItems( lootContext ) ) {
					spawnReward( extraReward, player, level, event.getHookEntity() );

					rewards.add( extraReward.getDisplayName()
						.getString() );
					extraRewardsCounter++;
				}
			}

		boolean isRaining = ( level instanceof ServerLevel && level.isRaining() );
		if( tryIncreaseFishingFanaticLevel( player, isRaining ) ) {
			player.displayClientMessage( new TranslatableComponent( "wonderful_enchantments.fanatic_level_up" ).withStyle( ChatFormatting.BOLD ),
				true
			);
		} else if( rewards.size() > 1 )
			notifyPlayerAboutRewards( rewards, player );

		event.damageRodBy( event.getRodDamage() + extraRewardsCounter );
		level.addFreshEntity( new ExperienceOrb( level, player.getX(), player.getY() + 0.5, player.getZ() + 0.5,
			extraRewardsCounter + MajruszLibrary.RANDOM.nextInt( 2 * extraRewardsCounter + 1 )
		) );
	}

	/** Generates fishing loot context for given player. */
	protected static LootContext generateLootContext( Player player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerLevel )player.level );
		lootContextBuilder.withParameter( LootContextParams.TOOL, player.getMainHandItem() )
			.withRandom( MajruszLibrary.RANDOM )
			.withLuck( player.getLuck() )
			.withParameter( LootContextParams.ORIGIN, player.position() );

		return lootContextBuilder.create( LootContextParamSets.FISHING );
	}

	/** Returns loot table at given resource location. (possible items to get) */
	protected static LootTable getLootTable( ResourceLocation location ) {
		return ServerLifecycleHooks.getCurrentServer()
			.getLootTables()
			.get( location );
	}

	/** Returns fishing loot table. (possible items to get) */
	protected static LootTable getFishingLootTable() {
		return getLootTable( BuiltInLootTables.FISHING );
	}

	/** Spawns item entity in the level and sets motion towards the player. */
	protected static void spawnReward( ItemStack reward, Player player, Level level, FishingHook hook ) {
		Vec3 spawnPosition = hook.position()
			.add( Random.getRandomVector3d( -0.25, 0.25, 0.125, 0.5, -0.25, 0.25 ) );

		ItemEntity itemEntity = new ItemEntity( level, spawnPosition.x, spawnPosition.y, spawnPosition.z, reward );
		Vec3 motion = player.position()
			.subtract( itemEntity.position() )
			.multiply( 0.1, 0.1, 0.1 );

		itemEntity.setDeltaMovement( motion.add( 0.0, Math.pow( VectorHelper.length( motion ), 0.5 ) * 0.25, 0.0 ) );
		level.addFreshEntity( itemEntity );
	}

	/**
	 Tries to increase level after player fished an item. Chance is higher when it is raining.

	 @return Returns whether the level was increased or not.
	 */
	protected static boolean tryIncreaseFishingFanaticLevel( Player player, boolean isRaining ) {
		FanaticEnchantment enchantment = Instances.FISHING_FANATIC;
		int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel( enchantment, player );

		if( enchantment.shouldLevelBeIncreased( enchantmentLevel, isRaining ) ) {
			ItemStack fishingRod = player.getMainHandItem();

			if( enchantmentLevel == 0 )
				fishingRod.enchant( enchantment, 1 );
			else {
				ListTag nbt = fishingRod.getEnchantmentTags();

				for( int i = 0; i < nbt.size(); ++i ) {
					CompoundTag enchantmentData = nbt.getCompound( i );
					String enchantmentID = enchantmentData.getString( "id" );

					if( enchantmentID.contains( "fishing_fanatic" ) ) {
						enchantmentData.putInt( "lvl", enchantmentLevel + 1 );
						break;
					}
				}

				fishingRod.addTagElement( "Enchantments", nbt );
			}

			ServerPlayer serverPlayer = CommonHelper.castIfPossible( ServerPlayer.class, player );
			if( serverPlayer != null ) {
				BasicTrigger basicTrigger = Instances.BASIC_TRIGGER;
				if( isRaining )
					basicTrigger.trigger( serverPlayer, "nothing_can_stop_me" );
				if( enchantmentLevel+1 == 1 )
					basicTrigger.trigger( serverPlayer, "fishing_fanatic" );
				if( enchantmentLevel+1 == 8 )
					basicTrigger.trigger( serverPlayer, "fishing_fanatic_true" );
			}

			return true;
		}

		return false;
	}

	/** Displays custom information on player's screen about fished items. */
	protected static void notifyPlayerAboutRewards( Multiset< String > rewards, Player player ) {
		TextComponent message = new TextComponent( ChatFormatting.WHITE + "(" );

		ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards )
			.elementSet()
			.asList();
		for( int i = 0; i < rewardList.size(); i++ ) {
			message.append( new TextComponent( ( ( i == 0 ) ? ChatFormatting.WHITE : ChatFormatting.GOLD ) + rewardList.get( i ) ) );

			if( rewards.count( rewardList.get( i ) ) > 1 )
				message.append( new TextComponent( ChatFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );

			if( i != rewardList.size() - 1 )
				message.append( new TextComponent( ChatFormatting.WHITE + ", " ) );
		}

		message.append( new TextComponent( ChatFormatting.WHITE + ")" ) );
		player.displayClientMessage( message, true );
	}

	/** Returns a chance to increase Fishing Fanatic level. */
	protected double getChanceForLevelIncrease( int level ) {
		if( level < 6 ) {
			return ( 6 - level ) * this.levelIncreaseChanceMultiplier.get();
		} else {
			return ( this.getMaxLevel() - level ) * this.highLevelIncreaseChanceMultiplier.get();
		}
	}

	/** Returns a chance to increase Fishing Fanatic level. (and multiplies it by config value if it is raining) */
	protected double getChanceForLevelIncrease( int level, boolean isRaining ) {
		return getChanceForLevelIncrease( level ) * ( isRaining ? this.rainingMultiplier.get() : 1.0 );
	}

	/** Returns whether level should be increased after fishing an item. */
	protected boolean shouldLevelBeIncreased( int currentLevel, boolean isRaining ) {
		return !isDisabled() && Random.tryChance( this.getChanceForLevelIncrease( currentLevel, isRaining ) );
	}

	/** Returns whether current drop should be generated from special loot table. */
	protected boolean shouldDropSpecialLoot( int level ) {
		return this.specialDropMinimumLevel.get() <= level && Random.tryChance( this.specialDropChance.get() );
	}
}
