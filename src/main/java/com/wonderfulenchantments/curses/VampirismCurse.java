package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.LevelHelper;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Weakens entity when it is outside during the day. */
@Mod.EventBusSubscriber
public class VampirismCurse extends WonderfulCurse {
	private static final MobEffect[] EFFECTS = new MobEffect[]{ MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.HUNGER };
	private static final String VAMPIRISM_TAG = "CurseOfVampirismCounter";
	private static final String VAMPIRISM_PARTICLE_TAG = "CurseOfVampirismParticleCounter";
	protected final DurationConfig effectDuration;
	protected final DurationConfig effectCooldown;

	public VampirismCurse() {
		super( "vampirism_curse", Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "Vampirism" );
		String durationComment = "Duration of negative effects. (in seconds)";
		String cooldownComment = "Cooldown between applying negative effects. (in seconds)";
		this.effectDuration = new DurationConfig( "effect_duration", durationComment, false, 30.0, 10.0, 300.0 );
		this.effectCooldown = new DurationConfig( "effect_cooldown", cooldownComment, false, 2.0, 1.0, 60.0 );
		this.curseGroup.addConfigs( this.effectDuration, this.effectCooldown );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		if( !( entity.level instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )entity.level;
		VampirismCurse vampirism = Instances.VAMPIRISM;
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( vampirism, entity.getArmorSlots() );
		CompoundTag data = entity.getPersistentData();

		int counter = data.getInt( VAMPIRISM_TAG ) + 1;
		int particleCounter = data.getInt( VAMPIRISM_PARTICLE_TAG ) + 1;
		if( enchantmentLevel > 0 && LevelHelper.isEntityOutsideDuringTheDay( entity ) ) {
			int particleCooldown = 9 - Math.min( enchantmentLevel, 4 ) * 2;
			if( particleCounter > particleCooldown ) {
				particleCounter -= particleCooldown;
				world.sendParticles( ParticleTypes.SMOKE, entity.getX(), entity.getY( 0.75 ), entity.getZ(), 1 + enchantmentLevel / 2,
					0.1, 0.25, 0.1, 0.01
				);
			}
			if( counter > vampirism.effectCooldown.getDuration() ) {
				counter -= vampirism.effectCooldown.getDuration();
				for( MobEffect effect : EFFECTS )
					EffectHelper.applyEffectIfPossible( entity, effect, vampirism.effectDuration.getDuration() * enchantmentLevel, 0 );

				entity.setSecondsOnFire( 3 + 2 * enchantmentLevel );
			}
		}
		data.putInt( VAMPIRISM_TAG, counter );
		data.putInt( VAMPIRISM_PARTICLE_TAG, particleCounter );
	}
}
