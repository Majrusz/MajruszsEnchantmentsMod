package com.wonderfulenchantments.enchantments;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.config.DoubleConfig;
import com.mlib.config.IntegerConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
		super( "fishing_fanatic", Rarity.UNCOMMON, EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND, "FishingFanatic" );

		String increaseComment = "Chance for increasing enchantment level per every missing level to 6th level. (for example if this value is equal 0.01 then to get 1st level you have 6 * 0.01 = 6% chance, to get 2nd level ( 6-1 ) * 0.01 = 5% chance)";
		this.levelIncreaseChanceMultiplier = new DoubleConfig( "level_increase_chance", increaseComment, false, 0.01, 0.01, 0.15 );

		String highIncreaseComment = "Chance for increasing enchantment level per every missing level from 6th to 8th level. (for example if this value is equal 0.002 then to get 7th level you have 2 * 0.002 = 0.4% chance and to get 8th level 1 * 0.002 = 0.2% chance)";
		this.highLevelIncreaseChanceMultiplier = new DoubleConfig( "high_level_increase_chance", highIncreaseComment, false, 0.002, 0.01, 0.15 );

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
	public float calcDamageByCreature( int level, CreatureAttribute creature ) {
		return ( float )( level * this.damageBonus.get() );
	}

	/** Method that displays enchantment name. It is overridden because at maximum level the enchantment will change its name. */
	@Override
	public ITextComponent getDisplayName( int level ) {
		if( level == this.getMaxLevel() ) {
			IFormattableTextComponent output = new TranslationTextComponent( "wonderful_enchantments.true_level" );

			return output.appendString( " " )
				.append( new TranslationTextComponent( this.getName() ) )
				.mergeStyle( TextFormatting.GRAY );
		}

		return super.getDisplayName( level );
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@SubscribeEvent
	public static void onFishedItem( ItemFishedEvent event ) {
		PlayerEntity player = event.getPlayer();
		World world = player.getEntityWorld();
		FanaticEnchantment enchantment = Instances.FISHING_FANATIC;
		LootContext lootContext = generateLootContext( player );
		LootTable standardLootTable = getFishingLootTable(), specialLootTable = getLootTable( SPECIAL_LOOT_TABLE );
		int fanaticLevel = EnchantmentHelper.getMaxEnchantmentLevel( enchantment, player );

		Multiset< String > rewards = HashMultiset.create();
		rewards.add( event.getDrops()
			.get( 0 )
			.getDisplayName()
			.getString() );

		int extraRewardsCounter = 0;
		for( int i = 0; i < fanaticLevel && enchantment.availabilityConfig.isEnabled(); i++ )
			if( Random.tryChance( enchantment.extraLootChance.get() ) ) {
				LootTable lootTable = enchantment.shouldDropSpecialLoot( fanaticLevel ) ? specialLootTable : standardLootTable;
				for( ItemStack extraReward : lootTable.generate( lootContext ) ) {
					spawnReward( extraReward, player, world, event.getHookEntity() );

					rewards.add( extraReward.getDisplayName()
						.getString() );
					extraRewardsCounter++;
				}
			}

		boolean isRaining = ( world instanceof ServerWorld && world.isRaining() );
		if( tryIncreaseFishingFanaticLevel( player, isRaining ) ) {
			player.sendStatusMessage( new TranslationTextComponent( "wonderful_enchantments.fanatic_level_up" ).mergeStyle( TextFormatting.BOLD ),
				true
			);
		} else if( rewards.size() > 1 )
			notifyPlayerAboutRewards( rewards, player );

		event.damageRodBy( event.getRodDamage() + extraRewardsCounter );
		world.addEntity( new ExperienceOrbEntity( world, player.getPosX(), player.getPosY() + 0.5, player.getPosZ() + 0.5,
			extraRewardsCounter + MajruszLibrary.RANDOM.nextInt( 2 * extraRewardsCounter + 1 )
		) );
	}

	/** Generates fishing loot context for given player. */
	protected static LootContext generateLootContext( PlayerEntity player ) {
		LootContext.Builder lootContextBuilder = new LootContext.Builder( ( ServerWorld )player.getEntityWorld() );
		lootContextBuilder.withParameter( LootParameters.TOOL, player.getHeldItemMainhand() )
			.withRandom( MajruszLibrary.RANDOM )
			.withLuck( player.getLuck() )
			.withParameter( LootParameters.field_237457_g_, player.getPositionVec() );

		return lootContextBuilder.build( LootParameterSets.FISHING );
	}

	/** Returns loot table at given resource location. (possible items to get) */
	protected static LootTable getLootTable( ResourceLocation location ) {
		return ServerLifecycleHooks.getCurrentServer()
			.getLootTableManager()
			.getLootTableFromLocation( location );
	}

	/** Returns fishing loot table. (possible items to get) */
	protected static LootTable getFishingLootTable() {
		return getLootTable( LootTables.GAMEPLAY_FISHING );
	}

	/** Spawns item entity in the world and sets motion towards the player. */
	protected static void spawnReward( ItemStack reward, PlayerEntity player, World world, FishingBobberEntity bobberEntity ) {
		Vector3d spawnPosition = bobberEntity.getPositionVec()
			.add( Random.getRandomVector3d( -0.25, 0.25, 0.125, 0.5, -0.25, 0.25 ) );

		ItemEntity itemEntity = new ItemEntity( world, spawnPosition.x, spawnPosition.y, spawnPosition.z, reward );
		Vector3d motion = player.getPositionVec()
			.subtract( itemEntity.getPositionVec() )
			.mul( 0.1, 0.1, 0.1 );

		itemEntity.setMotion( motion.add( 0.0, Math.pow( motion.squareDistanceTo( 0.0, 0.0, 0.0 ), 0.5 ) * 0.25, 0.0 ) );
		world.addEntity( itemEntity );
	}

	/**
	 Tries to increase level after player fished an item. Chance is higher when it is raining.

	 @return Returns whether the level was increased or not.
	 */
	protected static boolean tryIncreaseFishingFanaticLevel( PlayerEntity player, boolean isRaining ) {
		FanaticEnchantment enchantment = Instances.FISHING_FANATIC;
		int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( enchantment, player );

		if( enchantment.shouldLevelBeIncreased( enchantmentLevel, isRaining ) ) {
			ItemStack fishingRod = player.getHeldItemMainhand();

			if( enchantmentLevel == 0 )
				fishingRod.addEnchantment( enchantment, 1 );
			else {
				ListNBT nbt = fishingRod.getEnchantmentTagList();

				for( int i = 0; i < nbt.size(); ++i ) {
					CompoundNBT enchantmentData = nbt.getCompound( i );
					String enchantmentID = enchantmentData.getString( "id" );

					if( enchantmentID.contains( "fishing_fanatic" ) ) {
						enchantmentData.putInt( "lvl", enchantmentLevel + 1 );
						break;
					}
				}

				fishingRod.setTagInfo( "Enchantments", nbt );
			}

			return true;
		}

		return false;
	}

	/** Displays custom information on player's screen about fished items. */
	protected static void notifyPlayerAboutRewards( Multiset< String > rewards, PlayerEntity player ) {
		StringTextComponent message = new StringTextComponent( TextFormatting.WHITE + "(" );

		ImmutableList< String > rewardList = Multisets.copyHighestCountFirst( rewards )
			.elementSet()
			.asList();
		for( int i = 0; i < rewardList.size(); i++ ) {
			message.append( new StringTextComponent( ( ( i == 0 ) ? TextFormatting.WHITE : TextFormatting.GOLD ) + rewardList.get( i ) ) );

			if( rewards.count( rewardList.get( i ) ) > 1 )
				message.append( new StringTextComponent( TextFormatting.GOLD + " x" + rewards.count( rewardList.get( i ) ) ) );

			if( i != rewardList.size() - 1 )
				message.append( new StringTextComponent( TextFormatting.WHITE + ", " ) );
		}

		message.append( new StringTextComponent( TextFormatting.WHITE + ")" ) );
		player.sendStatusMessage( message, true );
	}

	/** Returns a chance to increase Fishing Fanatic level. */
	protected double getChanceForLevelIncrease( int level ) {
		if( level < 6 ) {
			return ( 6 - level ) * this.levelIncreaseChanceMultiplier.get();
		} else {
			return  ( this.getMaxLevel() - level ) * this.highLevelIncreaseChanceMultiplier.get();
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
