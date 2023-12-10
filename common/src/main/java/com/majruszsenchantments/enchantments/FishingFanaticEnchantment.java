package com.majruszsenchantments.enchantments;

import com.majruszlibrary.annotation.AutoInstance;
import com.majruszlibrary.collection.DefaultMap;
import com.majruszlibrary.data.Reader;
import com.majruszlibrary.entity.AttributeHandler;
import com.majruszlibrary.events.OnEntityPreDamaged;
import com.majruszlibrary.events.OnFishingExtraItemsGet;
import com.majruszlibrary.events.OnItemEquipped;
import com.majruszlibrary.events.OnItemFished;
import com.majruszlibrary.events.base.Priority;
import com.majruszlibrary.item.*;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.math.Range;
import com.majruszlibrary.text.TextHelper;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoInstance
public class FishingFanaticEnchantment extends Handler {
	final AttributeHandler attackSpeed;
	ResourceLocation specialDropId = MajruszsEnchantments.HELPER.getLocation( "gameplay/fishing/fishing_fanatic_extra" );
	Map< String, Float > levelUpChance = DefaultMap.of(
		DefaultMap.defaultEntry( 0.06f ),
		DefaultMap.entry( "level_2", 0.05f ),
		DefaultMap.entry( "level_3", 0.04f ),
		DefaultMap.entry( "level_4", 0.03f ),
		DefaultMap.entry( "level_5", 0.02f ),
		DefaultMap.entry( "level_6", 0.01f ),
		DefaultMap.entry( "level_7", 0.004f ),
		DefaultMap.entry( "level_8", 0.002f )
	);
	Map< String, Float > specialDropChance = DefaultMap.of(
		DefaultMap.defaultEntry( 0.0f ),
		DefaultMap.entry( "level_4", 0.0025f ),
		DefaultMap.entry( "level_5", 0.0075f ),
		DefaultMap.entry( "level_6", 0.02f ),
		DefaultMap.entry( "level_7", 0.04f ),
		DefaultMap.entry( "level_8", 0.06f )
	);
	float extraLootChance = 0.33333f;
	float rainMultiplier = 2.0f;
	float damageBonus = 1.0f;
	float attackSpeedMultiplier = 0.82f;

	public static CustomEnchantment create() {
		return new CustomEnchantment() {
			@Override
			public Component getFullname( int level ) {
				if( level == this.getMaxLevel() ) {
					return TextHelper.translatable( "enchantment.majruszsenchantments.fishing_fanatic.true" ).withStyle( ChatFormatting.GRAY );
				}

				return super.getFullname( level );
			}

			@Override
			public boolean isTradeable() {
				return false;
			}

			@Override
			public boolean isTreasureOnly() {
				return true;
			}
		}
			.rarity( Enchantment.Rarity.UNCOMMON )
			.category( EnchantmentCategory.FISHING_ROD )
			.slots( EquipmentSlots.HANDS )
			.maxLevel( 8 )
			.minLevelCost( level->level * 10 )
			.maxLevelCost( level->level * 10 + 20 );
	}

	public FishingFanaticEnchantment() {
		super( MajruszsEnchantments.FISHING_FANATIC, FishingFanaticEnchantment.class, false );

		this.attackSpeed = new AttributeHandler( "%s_attack_speed".formatted( this.enchantment.getId() ), ()->Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL );

		OnFishingExtraItemsGet.listen( this::increaseLoot )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.player ) );

		OnEntityPreDamaged.listen( this::increaseDamage )
			.addCondition( OnEntityPreDamaged::willTakeFullDamage )
			.addCondition( data->data.attacker != null )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, data.attacker ) );

		OnItemEquipped.listen( this::reduceAttackSpeed );

		OnItemFished.listen( this::tryToLevelUp )
			.priority( Priority.LOW );

		this.config.define( "extra_loot_chance", Reader.number(), s->this.extraLootChance, ( s, v )->this.extraLootChance = Range.CHANCE.clamp( v ) )
			.define( "level_up_chance", Reader.map( Reader.number() ), s->this.levelUpChance, ( s, v )->this.levelUpChance = Range.CHANCE.clamp( DefaultMap.of( v ) ) )
			.define( "level_up_chance_rain_multiplier", Reader.number(), s->this.rainMultiplier, ( s, v )->this.rainMultiplier = Range.of( 1.0f, 10.0f )
				.clamp( v ) )
			.define( "special_drop_chance", Reader.map( Reader.number() ), s->this.specialDropChance, ( s, v )->this.specialDropChance = Range.CHANCE.clamp( DefaultMap.of( v ) ) )
			.define( "special_drop_id", Reader.location(), s->this.specialDropId, ( s, v )->this.specialDropId = v )
			.define( "damage_bonus_per_level", Reader.number(), s->this.damageBonus, ( s, v )->this.damageBonus = Range.of( 0.0f, 10.0f ).clamp( v ) )
			.define( "attack_speed_multiplier_per_level", Reader.number(), s->this.attackSpeedMultiplier, ( s, v )->this.attackSpeedMultiplier = Range.CHANCE.clamp( v ) );
	}

	private void increaseLoot( OnFishingExtraItemsGet data ) {
		ItemStack fishingRod = data.fishingRod;
		int level = EnchantmentHelper.getLevel( this.enchantment, fishingRod );
		List< ItemStack > loot = this.spawnExtraLoot( data, level );

		if( !loot.isEmpty() ) {
			data.extraExperience += loot.size() + Random.nextInt( 2 * loot.size() ) + 1;
			data.extraItems.addAll( loot );
		}
	}

	private List< ItemStack > spawnExtraLoot( OnFishingExtraItemsGet data, int level ) {
		LootParams lootParams = LootHelper.toGiftParams( data.player );
		LootTable standard = LootHelper.getLootTable( BuiltInLootTables.FISHING );
		LootTable special = LootHelper.getLootTable( this.specialDropId );
		List< ItemStack > extraLoot = new ArrayList<>();
		for( int idx = 0; idx < level; idx++ ) {
			if( Random.check( this.extraLootChance ) ) {
				LootTable lootTable = Random.check( this.specialDropChance.get( "level_%d".formatted( level ) ) ) ? special : standard;
				extraLoot.addAll( lootTable.getRandomItems( lootParams ) );
			}
		}

		return extraLoot;
	}

	private void tryToLevelUp( OnItemFished data ) {
		boolean isRaining = data.getLevel().isRaining();
		float chanceMultiplier = isRaining ? this.rainMultiplier : 1.0f;
		if( !this.isEnabled ) {
			chanceMultiplier = 0.0f;
		}
		ItemStack fishingRod = ItemHelper.getHandItem( data.player, itemStack->itemStack.getItem() instanceof FishingRodItem );
		int level = EnchantmentHelper.getLevel( this.enchantment, fishingRod );
		if( level == this.enchantment.get().getMaxLevel() || !Random.check( this.levelUpChance.get( "level_%d".formatted( level ) ) * chanceMultiplier ) ) {
			return;
		}

		EnchantmentHelper.increaseLevel( this.enchantment, fishingRod );
		++level;
		if( data.player instanceof ServerPlayer player ) {
			if( level == 1 ) {
				MajruszsEnchantments.HELPER.triggerAchievement( player, "fishing_fanatic" );
			}
			if( level == this.enchantment.get().getMaxLevel() ) {
				MajruszsEnchantments.HELPER.triggerAchievement( player, "fishing_fanatic_true" );
			}
			if( isRaining ) {
				MajruszsEnchantments.HELPER.triggerAchievement( player, "fishing_fanatic_rain" );
			}
		}
		data.player.displayClientMessage( TextHelper.translatable( "enchantment.majruszsenchantments.fishing_fanatic.level_up" )
			.withStyle( ChatFormatting.BOLD ), true );
	}

	private void increaseDamage( OnEntityPreDamaged data ) {
		data.damage += EnchantmentHelper.getLevel( this.enchantment, data.attacker ) * this.damageBonus;
		data.spawnMagicParticles = true;
		ItemHelper.damage( data.attacker, EquipmentSlot.MAINHAND, 1 );
	}

	private void reduceAttackSpeed( OnItemEquipped data ) {
		float multiplier = ( float )Math.pow( this.attackSpeedMultiplier, EnchantmentHelper.getLevel( this.enchantment, data.entity ) );
		attackSpeed.setValue( multiplier - 1.0f ).apply( data.entity );
	}
}
