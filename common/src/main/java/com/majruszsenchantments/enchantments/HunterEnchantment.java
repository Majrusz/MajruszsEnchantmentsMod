package com.majruszsenchantments.enchantments;

import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszsenchantments.common.Handler;
import com.mlib.annotation.AutoInstance;
import com.mlib.contexts.OnEntityPreDamaged;
import com.mlib.contexts.OnLootingLevelGet;
import com.mlib.item.CustomEnchantment;
import com.mlib.item.EnchantmentHelper;
import com.mlib.item.EquipmentSlots;
import com.mlib.math.Range;
import com.mlib.mixininterfaces.IMixinProjectile;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoInstance
public class HunterEnchantment extends Handler {
	float penaltyMultiplier = -0.1f;
	float distanceMultiplier = 0.01f;

	public static CustomEnchantment create() {
		return new CustomEnchantment()
			.rarity( Enchantment.Rarity.RARE )
			.category( MajruszsEnchantments.IS_BOW_OR_CROSSBOW )
			.slots( EquipmentSlots.HANDS )
			.maxLevel( 3 )
			.minLevelCost( level->level * 9 + 6 )
			.maxLevelCost( level->level * 9 + 26 );
	}

	public HunterEnchantment() {
		super( MajruszsEnchantments.HUNTER, false );

		OnLootingLevelGet.listen( this::increaseLevel )
			.addCondition( data->data.source != null )
			.addCondition( data->data.source.is( DamageTypeTags.IS_PROJECTILE ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, this.getItemStack( data.source ) ) );

		OnEntityPreDamaged.listen( this::modifyDamage )
			.addCondition( data->data.attacker != null )
			.addCondition( data->data.source != null )
			.addCondition( data->data.source.is( DamageTypeTags.IS_PROJECTILE ) )
			.addCondition( data->EnchantmentHelper.has( this.enchantment, this.getItemStack( data.source ) ) );

		this.config.defineFloat( "penalty_multiplier_per_level", ()->this.penaltyMultiplier, x->this.penaltyMultiplier = Range.of( -1.0f, 0.0f ).clamp( x ) );
		this.config.defineFloat( "distance_multiplier_per_level", ()->this.distanceMultiplier, x->this.distanceMultiplier = Range.of( 0.0f, 10.0f )
			.clamp( x ) );
	}

	private void increaseLevel( OnLootingLevelGet data ) {
		data.level += EnchantmentHelper.getLevel( this.enchantment, this.getItemStack( data.source ) );
	}

	private void modifyDamage( OnEntityPreDamaged data ) {
		float distance = Math.max( data.target.distanceTo( data.attacker ) - 1.0f, 0.0f );
		int level = EnchantmentHelper.getLevel( this.enchantment, this.getItemStack( data.source ) );
		float multiplier = level * ( this.penaltyMultiplier + distance * this.distanceMultiplier );

		data.damage *= Math.max( 1.0f + multiplier, 0.0f );
		if( multiplier > 0.0f ) {
			data.spawnMagicParticles = true;
		}
	}

	private ItemStack getItemStack( DamageSource source ) {
		return IMixinProjectile.mlib$getProjectileWeapon( source.getDirectEntity() );
	}
}
