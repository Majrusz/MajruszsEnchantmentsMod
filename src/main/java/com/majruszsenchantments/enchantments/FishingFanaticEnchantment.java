package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.modhelper.AutoInstance;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleArrayConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.contexts.base.Condition;
import com.mlib.contexts.base.ModConfigs;
import com.mlib.contexts.base.Priority;
import com.mlib.contexts.*;
import com.mlib.items.ItemHelper;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FishingFanaticEnchantment extends CustomEnchantment {
	public FishingFanaticEnchantment() {
		this.rarity( Rarity.UNCOMMON )
			.category( EnchantmentCategory.FISHING_ROD )
			.slots( EquipmentSlots.BOTH_HANDS )
			.maxLevel( 8 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 20 );
	}

	@Override
	public Component getFullname( int level ) {
		if( level == this.getMaxLevel() ) {
			return Component.translatable( "enchantment.majruszsenchantments.fishing_fanatic.true" )
				.withStyle( ChatFormatting.GRAY, ChatFormatting.BOLD );
		}

		return super.getFullname( level );
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean canEnchant( ItemStack itemStack ) {
		return this.category.canEnchant( itemStack.getItem() );
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack itemStack ) {
		return false;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@AutoInstance
	public static class Handler {
		static final ResourceLocation SPECIAL_LOOT_TABLE = Registries.getLocation( "gameplay/fishing/fishing_fanatic_extra" );
		static final Function< Integer, String > LEVEL_FORMAT = idx->String.format( "level_%d", idx + 1 );
		final DoubleArrayConfig levelUpChances = new DoubleArrayConfig( LEVEL_FORMAT, Range.CHANCE, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01, 0.004, 0.002 );
		final DoubleArrayConfig specialDropChance = new DoubleArrayConfig( LEVEL_FORMAT, Range.CHANCE, 0.00, 0.00, 0.00, 0.0025, 0.0075, 0.02, 0.04, 0.06 );
		final DoubleConfig extraLootChance = new DoubleConfig( 0.33333, Range.CHANCE );
		final DoubleConfig rainMultiplier = new DoubleConfig( 2.0, new Range<>( 1.0, 10.0 ) );
		final DoubleConfig damageBonus = new DoubleConfig( 1.0, new Range<>( 0.0, 5.0 ) );
		final Supplier< FishingFanaticEnchantment > enchantment = Registries.FISHING_FANATIC;

		public Handler() {
			ConfigGroup group = ModConfigs.registerSubgroup( Registries.Groups.ENCHANTMENT )
				.name( "FishingFanatic" )
				.comment( "Gives a chance to catch additional items from fishing." );

			OnEnchantmentAvailabilityCheck.listen( OnEnchantmentAvailabilityCheck.ENABLE )
				.addCondition( OnEnchantmentAvailabilityCheck.is( this.enchantment ) )
				.addCondition( OnEnchantmentAvailabilityCheck.excludable() )
				.insertTo( group );

			OnExtraFishingLootCheck.listen( this::increaseLoot )
				.name( "Loot" )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.player ) )
				.addCondition( Condition.predicate( data->OnEnchantmentAvailabilityCheck.dispatch( this.enchantment.get() ).isEnabled() ) )
				.addConfig( this.specialDropChance.name( "SpecialDropChances" ).comment( "Chance for each extra item to be replaced with a better one." ) )
				.addConfig( this.extraLootChance.name( "extra_loot_chance" ).comment( "Independent chance for extra loot per enchantment level." ) )
				.insertTo( group );

			OnItemFished.listen( this::tryToLevelUp )
				.priority( Priority.LOWEST )
				.name( "LevelUp" )
				.addCondition( Condition.predicate( data->OnEnchantmentAvailabilityCheck.dispatch( this.enchantment.get() ).isEnabled() ) )
				.addConfig( this.levelUpChances.name( "Chances" ).comment( "Chances to acquire given enchantment level when an item is fished out." ) )
				.addConfig( this.rainMultiplier.name( "rain_multiplier" ).comment( "Chance multiplier when it rains." ) )
				.insertTo( group );

			OnEquipmentChanged.listen( Handler::giveExtremeAdvancement )
				.addCondition( Handler.hasBestFishingEnchantments() )
				.addCondition( Condition.predicate( data->data.entity instanceof ServerPlayer ) )
				.insertTo( group );

			OnPreDamaged.listen( this::increaseDamageDealt )
				.addCondition( Condition.hasEnchantment( this.enchantment, data->data.attacker ) )
				.addConfig( this.damageBonus.name( "damage_bonus" ).comment( "Amount of extra damage dealt by the fishing rod per enchantment level." ) )
				.insertTo( group );
		}

		private void increaseLoot( OnExtraFishingLootCheck.Data data ) {
			ItemStack fishingRod = data.fishingRod;
			int fanaticLevel = this.enchantment.get().getEnchantmentLevel( fishingRod );
			List< ItemStack > extraLoot = this.spawnExtraLoot( data, fanaticLevel );

			data.extraExperience += extraLoot.size() + Random.nextInt( 1, 2 * extraLoot.size() + 1 );
			data.extraLoot.addAll( extraLoot );
		}

		private List< ItemStack > spawnExtraLoot( OnExtraFishingLootCheck.Data data, int fanaticLevel ) {
			LootParams lootParams = data.generateLootParams();
			LootTable standardLootTable = getLootTable( BuiltInLootTables.FISHING );
			LootTable specialLootTable = getLootTable( SPECIAL_LOOT_TABLE );
			List< ItemStack > extraLoot = new ArrayList<>();

			for( int i = 0; i < fanaticLevel; i++ ) {
				if( Random.tryChance( this.extraLootChance.get() ) ) {
					LootTable lootTable = Random.tryChance( this.specialDropChance.get( fanaticLevel - 1 ) ) ? specialLootTable : standardLootTable;
					extraLoot.addAll( lootTable.getRandomItems( lootParams ) );
				}
			}

			return extraLoot;
		}

		private void tryToLevelUp( OnItemFished.Data data ) {
			boolean isRaining = data.getLevel().isRaining();
			double rainMultiplier = isRaining ? this.rainMultiplier.get() : 1.0;
			ItemStack fishingRod = ItemHelper.getMatchingHandItem( data.player, itemStack->itemStack.getItem() instanceof FishingRodItem );
			int fanaticLevel = this.enchantment.get().getEnchantmentLevel( fishingRod );
			if( fanaticLevel == this.enchantment.get().getMaxLevel() || !Random.tryChance( this.levelUpChances.get( fanaticLevel ) * rainMultiplier ) ) {
				return;
			}

			this.enchantment.get().increaseEnchantmentLevel( fishingRod );
			if( data.player instanceof ServerPlayer player ) {
				giveAdvancement( player, "fishing_fanatic", ()->fanaticLevel + 1 == 1 );
				giveAdvancement( player, "fishing_fanatic_true", ()->fanaticLevel + 1 == this.enchantment.get().getMaxLevel() );
				giveAdvancement( player, "nothing_can_stop_me", ()->isRaining );
			}
			sendLevelUpMessage( data.player );
		}

		private void increaseDamageDealt( OnPreDamaged.Data data ) {
			data.extraDamage += this.damageBonus.get() * this.enchantment.get().getEnchantmentLevel( data.attacker );
			data.spawnMagicParticles = true;
		}

		private static void giveAdvancement( ServerPlayer player, String type, Supplier< Boolean > check ) {
			if( check.get() ) {
				Registries.HELPER.triggerAchievement( player, type );
			}
		}

		private static void giveExtremeAdvancement( OnEquipmentChanged.Data data ) {
			giveAdvancement( ( ServerPlayer )data.entity, "fishing_fanatic_extreme", ()->true );
		}

		private static LootTable getLootTable( ResourceLocation location ) {
			return ServerLifecycleHooks.getCurrentServer().getLootData().getLootTable( location );
		}

		private static void sendLevelUpMessage( Player player ) {
			String keyId = "enchantment.majruszsenchantments.fishing_fanatic.level_up";

			player.displayClientMessage( Component.translatable( keyId ).withStyle( ChatFormatting.BOLD ), true );
		}

		private static Condition< OnEquipmentChanged.Data > hasBestFishingEnchantments() {
			return new Condition<>( data->{
				return ForgeRegistries.ENCHANTMENTS.getValues()
					.stream()
					.allMatch( enchantment->{
						return enchantment.isCurse()
							|| !enchantment.canApplyAtEnchantingTable( new ItemStack( Items.FISHING_ROD ) )
							|| EnchantmentHelper.getTagEnchantmentLevel( enchantment, data.event.getTo() ) == enchantment.getMaxLevel();
					} );
			} );
		}
	}
}
