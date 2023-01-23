package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.Registries;
import com.majruszsenchantments.gamemodifiers.EnchantmentModifier;
import com.mlib.EquipmentSlots;
import com.mlib.Random;
import com.mlib.annotations.AutoInstance;
import com.mlib.config.DoubleArrayConfig;
import com.mlib.config.DoubleConfig;
import com.mlib.enchantments.CustomEnchantment;
import com.mlib.gamemodifiers.Condition;
import com.mlib.gamemodifiers.GameModifier;
import com.mlib.gamemodifiers.contexts.OnEquipmentChanged;
import com.mlib.gamemodifiers.contexts.OnExtraFishingLootCheck;
import com.mlib.gamemodifiers.contexts.OnItemFished;
import com.mlib.gamemodifiers.contexts.OnPreDamaged;
import com.mlib.gamemodifiers.parameters.Priority;
import com.mlib.items.ItemHelper;
import com.mlib.math.Range;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
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
			.maxLevelCost( level->level * 10 + 20 )
			.setEnabledSupplier( Registries.getEnabledSupplier( Modifier.class ) );
	}

	@Override
	public Component getFullname( int level ) {
		if( level == this.getMaxLevel() ) {
			return new TranslatableComponent( "enchantment.majruszsenchantments.fishing_fanatic.true" )
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
	public static class Modifier extends EnchantmentModifier< FishingFanaticEnchantment > {
		static final ResourceLocation SPECIAL_LOOT_TABLE = Registries.getLocation( "gameplay/fishing/fishing_fanatic_extra" );
		static final Function< Integer, String > LEVEL_FORMAT = idx->String.format( "level_%d", idx + 1 );
		final DoubleArrayConfig levelUpChances = new DoubleArrayConfig( LEVEL_FORMAT, Range.CHANCE, 0.06, 0.05, 0.04, 0.03, 0.02, 0.01, 0.004, 0.002 );
		final DoubleArrayConfig specialDropChance = new DoubleArrayConfig( LEVEL_FORMAT, Range.CHANCE, 0.00, 0.00, 0.00, 0.0025, 0.0075, 0.02, 0.04, 0.06 );
		final DoubleConfig extraLootChance = new DoubleConfig( 0.33333, Range.CHANCE );
		final DoubleConfig rainMultiplier = new DoubleConfig( 2.0, new Range<>( 1.0, 10.0 ) );
		final DoubleConfig damageBonus = new DoubleConfig( 1.0, new Range<>( 0.0, 5.0 ) );

		public Modifier() {
			super( Registries.FISHING_FANATIC, Registries.Modifiers.ENCHANTMENT );

			new OnExtraFishingLootCheck.Context( this::increaseLoot )
				.name( "Loot" )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.player ) )
				.addCondition( this::isEnchantmentEnabled )
				.addConfig( this.specialDropChance.name( "SpecialDropChances" ).comment( "Chance for each extra item to be replaced with a better one." ) )
				.addConfig( this.extraLootChance.name( "extra_loot_chance" ).comment( "Independent chance for extra loot per enchantment level." ) )
				.insertTo( this );

			new OnItemFished.Context( this::tryToLevelUp )
				.priority( Priority.LOWEST )
				.name( "LevelUp" )
				.addCondition( this::isEnchantmentEnabled )
				.addConfig( this.levelUpChances.name( "Chances" ).comment( "Chances to acquire given enchantment level when an item is fished out." ) )
				.addConfig( this.rainMultiplier.name( "rain_multiplier" ).comment( "Chance multiplier when it rains." ) )
				.insertTo( this );

			new OnEquipmentChanged.Context( Modifier::giveExtremeAdvancement )
				.addCondition( new HasBestFishingEnchantments() )
				.addCondition( data->data.entity instanceof ServerPlayer )
				.insertTo( this );

			new OnPreDamaged.Context( this::increaseDamageDealt )
				.addCondition( new Condition.HasEnchantment<>( this.enchantment, data->data.attacker ) )
				.addConfig( this.damageBonus.name( "damage_bonus" ).comment( "Amount of extra damage dealt by the fishing rod per enchantment level." ) )
				.insertTo( this );

			this.name( "FishingFanatic" ).comment( "Gives a chance to catch additional items from fishing." );
		}

		private void increaseLoot( OnExtraFishingLootCheck.Data data ) {
			ItemStack fishingRod = data.fishingRod;
			int fanaticLevel = this.enchantment.get().getEnchantmentLevel( fishingRod );
			List< ItemStack > extraLoot = this.spawnExtraLoot( data, fanaticLevel );

			data.extraExperience += extraLoot.size() + Random.nextInt( 1, 2 * extraLoot.size() + 1 );
			data.extraRodDamage += extraLoot.size();
			data.extraLoot.addAll( extraLoot );
		}

		private List< ItemStack > spawnExtraLoot( OnExtraFishingLootCheck.Data data, int fanaticLevel ) {
			LootContext lootContext = data.generateLootContext();
			LootTable standardLootTable = getLootTable( BuiltInLootTables.FISHING );
			LootTable specialLootTable = getLootTable( SPECIAL_LOOT_TABLE );
			List< ItemStack > extraLoot = new ArrayList<>();

			for( int i = 0; i < fanaticLevel; i++ ) {
				if( Random.tryChance( this.extraLootChance.get() ) ) {
					LootTable lootTable = Random.tryChance( this.specialDropChance.get( fanaticLevel - 1 ) ) ? specialLootTable : standardLootTable;
					extraLoot.addAll( lootTable.getRandomItems( lootContext ) );
				}
			}

			return extraLoot;
		}

		private void tryToLevelUp( OnItemFished.Data data ) {
			boolean isRaining = data.level.isRaining();
			double rainMultiplier = isRaining ? this.rainMultiplier.get() : 1.0;
			ItemStack fishingRod = ItemHelper.getMatchingHandItem( data.player, itemStack->itemStack.getItem() instanceof FishingRodItem );
			int fanaticLevel = this.enchantment.get().getEnchantmentLevel( fishingRod );
			if( fanaticLevel == this.enchantment.get().getMaxLevel() || !Random.tryChance( this.levelUpChances.get( fanaticLevel ) * rainMultiplier ) ) {
				return;
			}

			this.enchantment.get().increaseEnchantmentLevel( fishingRod );
			if( data.player instanceof ServerPlayer player ) {
				giveAdvancement( player, "nothing_can_stop_me", ()->isRaining );
				giveAdvancement( player, "fishing_fanatic", ()->fanaticLevel + 1 == 1 );
				giveAdvancement( player, "fishing_fanatic_true", ()->fanaticLevel + 1 == this.enchantment.get().getMaxLevel() );
			}
			sendLevelUpMessage( data.player );
		}

		private void increaseDamageDealt( OnPreDamaged.Data data ) {
			data.extraDamage += this.damageBonus.get() * this.enchantment.get().getEnchantmentLevel( data.attacker );
			data.spawnMagicParticles = true;
		}

		private static void giveAdvancement( ServerPlayer player, String type, Supplier< Boolean > check ) {
			if( check.get() ) {
				Registries.BASIC_TRIGGER.trigger( player, type );
			}
		}

		private static void giveExtremeAdvancement( OnEquipmentChanged.Data data ) {
			giveAdvancement( ( ServerPlayer )data.entity, "fishing_fanatic_extreme", ()->true );
		}

		private static LootTable getLootTable( ResourceLocation location ) {
			return ServerLifecycleHooks.getCurrentServer().getLootTables().get( location );
		}

		private static void sendLevelUpMessage( Player player ) {
			String keyId = "enchantment.majruszsenchantments.fishing_fanatic.level_up";

			player.displayClientMessage( new TranslatableComponent( keyId ).withStyle( ChatFormatting.BOLD ), true );
		}

		public static class HasBestFishingEnchantments extends Condition< OnEquipmentChanged.Data > {
			@Override
			public boolean check( GameModifier feature, OnEquipmentChanged.Data data ) {
				return ForgeRegistries.ENCHANTMENTS.getValues()
					.stream()
					.allMatch( enchantment->{
						return enchantment.isCurse()
							|| !enchantment.canApplyAtEnchantingTable( new ItemStack( Items.FISHING_ROD ) )
							|| EnchantmentHelper.getItemEnchantmentLevel( enchantment, data.event.getTo() ) == enchantment.getMaxLevel();
					} );
			}
		}
	}
}
