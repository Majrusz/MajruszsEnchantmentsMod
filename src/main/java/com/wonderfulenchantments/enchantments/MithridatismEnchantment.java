package com.wonderfulenchantments.enchantments;

import com.mlib.Random;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.config.StringListConfig;
import com.mlib.effects.EffectHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that gives Absorption and Mithridatism Protection after any negative effect is applied to the player. */
public class MithridatismEnchantment extends WonderfulEnchantment {
	public MithridatismEnchantment() {
		super( "mithridatism", Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST, "Mithridatism" );

		setMaximumEnchantmentLevel( 4 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->( 15 + 100 * ( level - 1 ) ) );
	}

	/** Adds config group from to enchantment group. */
	public void addConfigGroup( ConfigGroup group ) {
		this.enchantmentGroup.addGroup( group );
	}

	/** Returns current Mithridatism enchantment level. */
	public int getItemEnchantmentLevel( LivingEntity entity ) {
		return getEnchantmentLevel( entity.getItemBySlot( EquipmentSlot.CHEST ) );
	}

	/** MobEffect that decreases damage from certain negative effects. */
	@Mod.EventBusSubscriber
	public static class MithridatismProtectionEffect extends MobEffect {
		protected final ConfigGroup effectGroup;
		protected final StringListConfig damageSourceList;
		protected final DoubleConfig absorptionPerLevel, baseDamageReduction, damageReductionPerLevel, levelUpChance;
		protected final DurationConfig duration;

		public MithridatismProtectionEffect( MithridatismEnchantment mithridatism ) {
			super( MobEffectCategory.BENEFICIAL, 0xff76db4c );

			String listComment = "Damage sources that deal less damage when the effect is active.";
			this.damageSourceList = new StringListConfig( "damage_source_list", listComment, false, "magic", "wither", "bleeding" );

			String absorptionComment = "Level of Absorption applied to the player per enchantment level rounded down. (minimum. 1lvl)";
			this.absorptionPerLevel = new DoubleConfig( "absorption_per_level", absorptionComment, false, 0.5, 0, 3 );

			String baseReductionComment = "Base amount of damage decreased from negative effects.";
			this.baseDamageReduction = new DoubleConfig( "base_reduction", baseReductionComment, false, 0.2, 0.0, 1.0 );

			String levelReductionComment = "Amount of damage decreased from negative effects per enchantment level.";
			this.damageReductionPerLevel = new DoubleConfig( "reduction_per_level", levelReductionComment, false, 0.1, 0.0, 1.0 );

			String durationComment = "Duration of both the Absorption and Mithridatism Protection. (in seconds)";
			this.duration = new DurationConfig( "duration", durationComment, false, 60.0, 2.0, 600.0 );

			String levelUpComment = "Chance for Mithridatism to increase its level.";
			this.levelUpChance = new DoubleConfig( "level_up_chance", levelUpComment, false, 0.025, 0.0, 1.0 );

			this.effectGroup = new ConfigGroup( "MithridatismProtection", "" );
			this.effectGroup.addConfigs( this.damageSourceList, this.absorptionPerLevel, this.baseDamageReduction, this.damageReductionPerLevel,
				this.levelUpChance, this.duration
			);

			mithridatism.addConfigGroup( this.effectGroup );
		}

		@SubscribeEvent
		public static void whenEffectApplied( PotionEvent.PotionAddedEvent event ) {
			MobEffectInstance effectInstance = event.getPotionEffect();
			MobEffect effect = effectInstance.getEffect();
			LivingEntity entity = event.getEntityLiving();
			MithridatismEnchantment mithridatism = Instances.MITHRIDATISM;
			MithridatismEnchantment.MithridatismProtectionEffect mithridatismEffect = Instances.MITHRIDATISM_PROTECTION;
			int mithridatismLevel = mithridatism.getItemEnchantmentLevel( entity );

			if( !effect.isBeneficial() && mithridatismLevel > 0 && !entity.hasEffect( mithridatismEffect ) ) {
				int duration = mithridatismEffect.getDuration();
				EffectHelper.applyEffectIfPossible( entity, mithridatismEffect, duration, mithridatismLevel - 1 );

				int absorptionAmplifier = Math.max( 0, mithridatismEffect.getAbsorptionLevel( entity ) - 1 );
				EffectHelper.applyEffectIfPossible( entity, MobEffects.ABSORPTION, duration, absorptionAmplifier );
			}
		}

		@SubscribeEvent
		public static void whenEffectRemoved( PotionEvent.PotionExpiryEvent event ) {
			MobEffectInstance effectInstance = event.getPotionEffect();
			if( effectInstance == null )
				return;

			MobEffect effect = effectInstance.getEffect();
			LivingEntity entity = event.getEntityLiving();
			MithridatismEnchantment mithridatism = Instances.MITHRIDATISM;
			MithridatismEnchantment.MithridatismProtectionEffect mithridatismEffect = Instances.MITHRIDATISM_PROTECTION;
			int mithridatismLevel = mithridatism.getItemEnchantmentLevel( entity );

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

			event.setAmount( ( float )( event.getAmount() * ( 1.0 - damageReduction ) ) );
		}

		/** Returns current damage reduction depending on enchantment level. */
		protected double getDamageReduction( LivingEntity entity ) {
			MobEffectInstance effectInstance = entity.getEffect( this );
			int mithridatismLevel = effectInstance != null ? effectInstance.getAmplifier() : 0;

			return mithridatismLevel == 0 ? 0.0 : Math.min( 1,
				mithridatismLevel * this.damageReductionPerLevel.get() + this.baseDamageReduction.get()
			);
		}

		/** Returns current Absorption level depending on enchantment level. */
		protected int getAbsorptionLevel( LivingEntity entity ) {
			int mithridatismLevel = Instances.MITHRIDATISM.getItemEnchantmentLevel( entity );

			return mithridatismLevel == 0 ? 0 : ( int )( Math.max( 0, mithridatismLevel * this.absorptionPerLevel.get() ) );
		}

		/** Returns Mithridatism effect duration. */
		protected int getDuration() {
			return this.duration.getDuration();
		}

		/** Checks whether given damage source is one from the effect list. */
		protected boolean isDamageAffected( DamageSource damageSource ) {
			return this.damageSourceList.contains( damageSource.getMsgId() );
		}

		/** Increases Mithridatism level for given player. */
		protected void increaseLevel( LivingEntity entity ) {
			ItemStack chestplate = entity.getItemBySlot( EquipmentSlot.CHEST );
			Instances.MITHRIDATISM.increaseEnchantmentLevel( chestplate );

			notifyAboutLevelUp( entity );
		}

		/** Notifies player when the Mithridatism level was increased. */
		protected void notifyAboutLevelUp( LivingEntity entity ) {
			if( !( entity instanceof Player ) )
				return;

			MutableComponent message = new TranslatableComponent( "wonderful_enchantments.mithridatism_level_up" );
			message.withStyle( ChatFormatting.BOLD );

			Player player = ( Player )entity;
			player.displayClientMessage( message, true );
		}
	}

}
