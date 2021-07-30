package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that makes explosion not destroy blocks when exploded near the player. (inspired by The Binding Of Isaac) */
@Mod.EventBusSubscriber
public class FuseCutterEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig maximumOffset;

	public FuseCutterEnchantment() {
		super( "fuse_cutter", Rarity.UNCOMMON, RegistryHandler.SHIELD, EquipmentSlots.BOTH_HANDS, "FuseCutter" );

		String distanceComment = "Maximum distance in blocks from player to entity.";
		this.maximumOffset = new DoubleConfig( "maximum_offset", distanceComment, false, 6.0, 1.0, 100.0 );
		this.enchantmentGroup.addConfigs( this.maximumOffset );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 32 );
		setMinimumEnchantabilityCalculator( level->8 );
	}

	@SubscribeEvent
	public static void onExplosion( ExplosionEvent.Start explosionEvent ) {
		FuseCutterEnchantment enchantment = Instances.FUSE_CUTTER;
		Explosion explosion = explosionEvent.getExplosion();
		int enchantmentLevel = enchantment.getMaximumEnchantmentLevelNearby( explosionEvent.getWorld(), explosion.getPosition() );

		if( enchantmentLevel == 0 )
			return;

		enchantment.cancelExplosion( explosionEvent );
	}

	/** Returns the highest level of the Fuse Cutter enchantment near given position. */
	protected int getMaximumEnchantmentLevelNearby( Level world, Vec3 position ) {
		double x = position.x, y = position.y, z = position.z, offset = this.maximumOffset.get();
		AABB axisAligned = new AABB( x - offset, y - offset, z - offset, x + offset, y + offset, z + offset );

		for( LivingEntity livingEntity : world.getEntitiesOfClass( LivingEntity.class, axisAligned ) ) {
			int mainHandEnchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel( this,
				livingEntity.getItemBySlot( EquipmentSlot.MAINHAND )
			);
			int offHandEnchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel( this,
				livingEntity.getItemBySlot( EquipmentSlot.OFFHAND )
			);

			if( livingEntity.isBlocking() && Math.max( offHandEnchantmentLevel, mainHandEnchantmentLevel ) > 0 )
				return 1;
		}

		return 0;
	}

	/** Disables explosion completely and spawns particle effects with sound. */
	protected void cancelExplosion( ExplosionEvent explosionEvent ) {
		explosionEvent.setCanceled( true );

		if( !( explosionEvent.getWorld() instanceof ServerLevel ) )
			return;

		ServerLevel world = ( ServerLevel )explosionEvent.getWorld();
		Explosion explosion = explosionEvent.getExplosion();
		Vec3 position = explosion.getPosition();

		for( int i = 0; i < 2; ++i )
			world.sendParticles( i == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, position.x, position.y + 0.5, position.z,
				16 * i, 0.125, 0.25, 0.125, 0.025
			);

		world.playSound( null, position.x, position.y, position.z, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.AMBIENT,
			1.0f, 1.0f
		);
	}
}
