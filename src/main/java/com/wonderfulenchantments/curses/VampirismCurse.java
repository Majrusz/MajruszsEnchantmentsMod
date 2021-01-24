package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import com.mlib.config.DurationConfig;
import com.mlib.effects.EffectHelper;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Weakens entity when it is outside during the day. */
@Mod.EventBusSubscriber
public class VampirismCurse extends WonderfulCurse {
	private static final Effect[] EFFECTS = new Effect[]{ Effects.WEAKNESS, Effects.SLOWNESS, Effects.HUNGER };
	private static final String VAMPIRISM_TAG = "CurseOfVampirismCounter";
	private static final String VAMPIRISM_PARTICLE_TAG = "CurseOfVampirismParticleCounter";
	protected final DurationConfig effectDuration;
	protected final DurationConfig effectCooldown;

	public VampirismCurse() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "Vampirism" );
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
		if( !( entity.world instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )entity.world;
		VampirismCurse vampirism = Instances.VAMPIRISM;
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( vampirism, entity.getArmorInventoryList() );
		CompoundNBT data = entity.getPersistentData();

		int counter = data.getInt( VAMPIRISM_TAG ) + 1;
		int particleCounter = data.getInt( VAMPIRISM_PARTICLE_TAG ) + 1;
		if( enchantmentLevel > 0 && isPlayerOutsideDuringTheDay( entity, world ) ) {
			int particleCooldown = 9 - Math.min( enchantmentLevel, 4 ) * 2;
			if( particleCounter > particleCooldown ) {
				particleCounter -= particleCooldown;
				world.spawnParticle( ParticleTypes.SMOKE, entity.getPosX(), entity.getPosYHeight( 0.75 ), entity.getPosZ(), 1 + enchantmentLevel/2, 0.1, 0.25, 0.1, 0.01 );
			}
			if( counter > vampirism.effectCooldown.getDuration() ) {
				counter -= vampirism.effectCooldown.getDuration();
				for( Effect effect : EFFECTS )
					EffectHelper.applyEffectIfPossible( entity, effect, vampirism.effectDuration.getDuration() * enchantmentLevel, 0 );

				entity.setFire( 3 + 2 * enchantmentLevel );
			}
		}
		data.putInt( VAMPIRISM_TAG, counter );
		data.putInt( VAMPIRISM_PARTICLE_TAG, particleCounter );
	}

	/** Checks whether player is outside during the day. */
	protected static boolean isPlayerOutsideDuringTheDay( LivingEntity entity, ServerWorld world ) {
		return world.canSeeSky( new BlockPos( entity.getPositionVec() ) ) && world.isDaytime();
	}
}
