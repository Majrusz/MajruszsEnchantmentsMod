package com.wonderfulenchantments.enchantments;

import com.mlib.MajruszLibrary;
import com.mlib.Random;
import com.mlib.config.*;
import com.mlib.effects.EffectHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/** Enchantment that gives Absorption and Mithridatism Protection after any negative effect is applied to the player. */
public class MithridatismEnchantment extends WonderfulEnchantment {
	public MithridatismEnchantment() {
		super( "mithridatism", Rarity.VERY_RARE, EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST, "Mithridatism" );

		setMaximumEnchantmentLevel( 3 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 + 100 * ( level - 1 ) ) );
	}

	/** Adds config group from to enchantment group. */
	public void addConfigGroup( ConfigGroup group ) {
		this.enchantmentGroup.addGroup( group );
	}

	/** Returns current Mithridatism enchantment level. */
	public int getEnchantmentLevel( LivingEntity entity ) {
		return EnchantmentHelper.getEnchantmentLevel( this, entity.getItemStackFromSlot( EquipmentSlotType.CHEST ) );
	}

	/** Effect that decreases damage from certain negative effects. */
	@Mod.EventBusSubscriber
	public static class MithridatismProtectionEffect extends Effect {
		protected final ConfigGroup effectGroup;
		protected final StringListConfig damageSourceList;
		protected final DoubleConfig absorptionPerLevel, baseDamageReduction, damageReductionPerLevel, levelUpChance;
		protected final DurationConfig duration;

		public MithridatismProtectionEffect( MithridatismEnchantment mithridatism ) {
			super( EffectType.BENEFICIAL, 0xff76db4c );

			String listComment = "Damage sources that will deal less damage when effect is active.";
			String absorptionComment = "Level of Absorption applied to the player per enchantment level.";
			String baseReductionComment = "Base amount of damage decreased from negative effects.";
			String levelReductionComment = "Amount of damage decreased from negative effects per enchantment level.";
			String durationComment = "Duration of both the Absorption and Mithridatism Protection. (in seconds)";
			String levelUpComment = "Chance for Mithridatism to increase its level.";
			this.effectGroup = new ConfigGroup( "MithridatismProtection", "" );
			this.damageSourceList = new StringListConfig( "damage_source_list", listComment, false, "magic", "wither", "bleeding" );
			this.absorptionPerLevel = new DoubleConfig( "absorption_per_level", absorptionComment, false, 0.75, 0, 3 );
			this.baseDamageReduction = new DoubleConfig( "base_reduction", baseReductionComment, false, 0.15, 0.0, 1.0 );
			this.damageReductionPerLevel = new DoubleConfig( "reduction_per_level", levelReductionComment, false, 0.15, 0.0, 1.0 );
			this.levelUpChance = new DoubleConfig( "level_up_chance", levelUpComment, false, 0.025, 0.0, 1.0 );
			this.duration = new DurationConfig( "duration", durationComment, false, 60.0, 2.0, 600.0 );
			this.effectGroup.addConfigs( this.damageSourceList, this.absorptionPerLevel, this.baseDamageReduction, this.damageReductionPerLevel, this.duration );

			mithridatism.addConfigGroup( this.effectGroup );
		}

		@SubscribeEvent
		public static void whenEffectApplied( PotionEvent.PotionAddedEvent event ) {
			EffectInstance effectInstance = event.getPotionEffect();
			Effect effect = effectInstance.getPotion();
			LivingEntity entity = event.getEntityLiving();
			MithridatismEnchantment mithridatism = Instances.MITHRIDATISM;
			MithridatismEnchantment.MithridatismProtectionEffect mithridatismEffect = Instances.MITHRIDATISM_PROTECTION;
			int mithridatismLevel = mithridatism.getEnchantmentLevel( entity );

			if( !effect.isBeneficial() && mithridatismLevel > 0 && !entity.isPotionActive( mithridatismEffect ) ) {
				int duration = mithridatismEffect.getDuration();
				int effectAmplifier = mithridatismEffect.getEffectAmplifier( entity );

				EffectHelper.applyEffectIfPossible( entity, mithridatismEffect, duration, mithridatismLevel - 1 );
				EffectHelper.applyEffectIfPossible( entity, Effects.ABSORPTION, duration, effectAmplifier );
			}
		}

		@SubscribeEvent
		public static void whenEffectRemoved( PotionEvent.PotionRemoveEvent event ) {
			EffectInstance effectInstance = event.getPotionEffect();
			if( effectInstance == null )
				return;

			Effect effect = effectInstance.getPotion();
			LivingEntity entity = event.getEntityLiving();
			MithridatismEnchantment mithridatism = Instances.MITHRIDATISM;
			MithridatismEnchantment.MithridatismProtectionEffect mithridatismEffect = Instances.MITHRIDATISM_PROTECTION;
			int mithridatismLevel = mithridatism.getEnchantmentLevel( entity );

			if( mithridatismLevel >= mithridatism.getMaxLevel() || mithridatismLevel == 0 || mithridatism.isDisabled() )
				return;

			if( effect.isBeneficial() || !Random.tryChance( mithridatismEffect.levelUpChance.get() ) )
				return;

			mithridatismEffect.increaseLevel( entity );
		}

		@SubscribeEvent
		public static void whenDamaged( LivingHurtEvent event ) {
			MithridatismProtectionEffect mithridatismEffect = Instances.MITHRIDATISM_PROTECTION;
			DamageSource damageSource = event.getSource();

			if( !mithridatismEffect.isDamageAffected( damageSource ) )
				return;

			double damageReduction = mithridatismEffect.getDamageReduction( event.getEntityLiving() );
			if( damageReduction == 0.0 )
				return;

			event.setAmount( ( float )( event.getAmount()*( 1.0-damageReduction ) ) );
		}

		/** Returns current damage reduction depending on enchantment level. */
		protected double getDamageReduction( LivingEntity entity ) {
			EffectInstance effectInstance = entity.getActivePotionEffect( this );
			int mithridatismLevel = effectInstance != null ? effectInstance.getAmplifier() : 0;

			return mithridatismLevel == 0 ? 0.0 : Math.min( 1, mithridatismLevel * this.damageReductionPerLevel.get() + this.baseDamageReduction.get() );
		}

		/** Returns current Absorption level depending on enchantment level. */
		protected int getEffectAmplifier( LivingEntity entity ) {
			int mithridatismLevel = Instances.MITHRIDATISM.getEnchantmentLevel( entity );

			return mithridatismLevel == 0 ? 0 : ( int )( Math.max( 1, mithridatismLevel * this.absorptionPerLevel.get() ) - 1 );
		}

		/** Returns Mithridatism effect duration. */
		protected int getDuration() {
			return this.duration.getDuration();
		}

		/** Checks whether given damage source is one from the effect list. */
		protected boolean isDamageAffected( DamageSource damageSource ) {
			return this.damageSourceList.contains( damageSource.getDamageType() );
		}

		/** Increases Mithridatism level for given player. */
		protected void increaseLevel( LivingEntity entity ) {
			ItemStack chestplate = entity.getItemStackFromSlot( EquipmentSlotType.CHEST );
			MithridatismEnchantment mithridatism = Instances.MITHRIDATISM;
			ListNBT listNBT = chestplate.getEnchantmentTagList();

			for( int i = 0; i < listNBT.size(); ++i ) {
				CompoundNBT compoundNBT = listNBT.getCompound( i );
				String enchantmentID = compoundNBT.getString( "id" );
				if( enchantmentID.contains( "mithridatism" ) ) {
					compoundNBT.putInt( "lvl", mithridatism.getEnchantmentLevel( entity )+1 );
					break;
				}
			}

			chestplate.setTagInfo( "Enchantments", listNBT );
		}
	}

}
