package com.wonderfulenchantments.enchantments;

import com.mlib.Random;
import com.mlib.attributes.AttributeHandler;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.wonderfulenchantments.Instances;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Gives a chance to completely ignore any damage source.. */
@Mod.EventBusSubscriber
public class DodgeEnchantment extends WonderfulEnchantment {
	private static final AttributeHandler ATTRIBUTE_HANDLER = new AttributeHandler( "ad3e064e-e9f6-4747-a86b-46dc4e2a1444", "KnockBackImmunityTime",
		Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADDITION
	);
	private static final String DODGE_TAG = "KnockbackImmunityCounter";
	protected final DoubleConfig dodgeChancePerLevel;
	protected final DoubleConfig damageAmountFactor;
	protected final DurationConfig immunityTime;

	public DodgeEnchantment() {
		super( "dodge", Rarity.RARE, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS, "Dodge" );
		String chanceComment = "Chance to completely ignore any damage source per enchantment level.";
		String damageComment = "Amount of damage converted to pants damage. (for example if this factor is equal 0.5 and player took 10 damage so its pants takes 5 damage)";
		String immunityComment = "Duration of knockback immunity after successful dodge. (in seconds)";
		this.dodgeChancePerLevel = new DoubleConfig( "dodge_chance", chanceComment, false, 0.125, 0.01, 0.4 );
		this.damageAmountFactor = new DoubleConfig( "damage_factor", damageComment, false, 0.5, 0.0, 10.0 );
		this.immunityTime = new DurationConfig( "immunity_duration", immunityComment, false, 3.0, 0.0, 30.0 );
		this.enchantmentGroup.addConfigs( this.dodgeChancePerLevel, this.damageAmountFactor, this.immunityTime );

		setMaximumEnchantmentLevel( 2 );
		setDifferenceBetweenMinimumAndMaximum( 20 );
		setMinimumEnchantabilityCalculator( level->( 14 * level ) );
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingDamageEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		ItemStack pants = entity.getItemBySlot( EquipmentSlot.LEGS );
		CompoundTag data = entity.getPersistentData();
		DodgeEnchantment dodge = Instances.DODGE;
		int dodgeLevel = EnchantmentHelper.getItemEnchantmentLevel( dodge, pants );

		if( dodgeLevel <= 0 || !( entity.level instanceof ServerLevel ) )
			return;

		if( !Random.tryChance( dodgeLevel * dodge.dodgeChancePerLevel.get() ) )
			return;

		updateImmunity( entity, dodge.immunityTime.getDuration() );
		spawnParticlesAndPlaySounds( entity );
		if( dodge.damageAmountFactor.get() > 0.0 )
			pants.hurtAndBreak( Math.max( ( int )( event.getAmount() * dodge.damageAmountFactor.get() ), 1 ), entity,
				owner->owner.broadcastBreakEvent( EquipmentSlot.LEGS )
			);
		event.setCanceled( true );
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		CompoundTag data = entity.getPersistentData();

		updateImmunity( entity, data.getInt( DODGE_TAG ) - 1 );
	}

	/** Updates current knockback immunity depending on dodge tag. */
	protected static void updateImmunity( LivingEntity entity, int duration ) {
		CompoundTag data = entity.getPersistentData();
		data.putInt( DODGE_TAG, Math.max( 0, duration ) );

		ATTRIBUTE_HANDLER.setValueAndApply( entity, data.getInt( DODGE_TAG ) > 0 ? 1.0 : 0.0 );
	}

	protected static void spawnParticlesAndPlaySounds( LivingEntity entity ) {
		ServerLevel world = ( ServerLevel )entity.level;
		for( double d = 0.0; d < 3.0; d++ ) {
			Vec3 emitterPosition = new Vec3( 0.0, entity.getBbHeight() * 0.25 * ( d + 1.0 ), 0.0 ).add( entity.position() );
			for( int i = 0; i < 2; i++ )
				world.sendParticles( i == 0 ? ParticleTypes.CAMPFIRE_COSY_SMOKE : ParticleTypes.LARGE_SMOKE, emitterPosition.x, emitterPosition.y,
					emitterPosition.z, 8 * ( i+1 ), 0.125, 0.0, 0.125, ( i == 0 ? 0.1 : 0.4 ) * 0.075
				);
		}
		world.playSound( null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
			SoundSource.AMBIENT, 1.0f, 1.0f
		);
	}
}
