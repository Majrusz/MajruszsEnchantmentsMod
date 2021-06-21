package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.mlib.config.DoubleConfig;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that makes explosion not destroy blocks when exploded near the player. (inspired by The Binding Of Isaac) */
@Mod.EventBusSubscriber
public class FuseCutterEnchantment extends WonderfulEnchantment {
	protected final DoubleConfig maximumOffset;

	public FuseCutterEnchantment() {
		super( "fuse_cutter", Rarity.UNCOMMON, RegistryHandler.SHIELD, EquipmentSlotTypes.BOTH_HANDS, "FuseCutter" );

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
	protected int getMaximumEnchantmentLevelNearby( World world, Vector3d position ) {
		double x = position.x, y = position.y, z = position.z, offset = this.maximumOffset.get();
		AxisAlignedBB axisAligned = new AxisAlignedBB( x - offset, y - offset, z - offset, x + offset, y + offset, z + offset );

		for( LivingEntity livingEntity : world.getEntitiesWithinAABB( LivingEntity.class, axisAligned ) ) {
			int mainHandEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel( this,
				livingEntity.getItemStackFromSlot( EquipmentSlotType.MAINHAND )
			);
			int offHandEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel( this,
				livingEntity.getItemStackFromSlot( EquipmentSlotType.OFFHAND )
			);

			if( livingEntity.isHandActive() && Math.max( offHandEnchantmentLevel, mainHandEnchantmentLevel ) > 0 )
				return 1;
		}

		return 0;
	}

	/** Disables explosion completely and spawns particle effects with sound. */
	protected void cancelExplosion( ExplosionEvent explosionEvent ) {
		explosionEvent.setCanceled( true );

		if( !( explosionEvent.getWorld() instanceof ServerWorld ) )
			return;

		ServerWorld world = ( ServerWorld )explosionEvent.getWorld();
		Explosion explosion = explosionEvent.getExplosion();
		Vector3d position = explosion.getPosition();

		for( int i = 0; i < 2; ++i )
			world.spawnParticle( i == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SMOKE, position.getX(), position.getY() + 0.5, position.getZ(),
				16 * i, 0.125, 0.25, 0.125, 0.025
			);

		world.playSound( null, position.getX(), position.getY(), position.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.AMBIENT,
			1.0f, 1.0f
		);
	}
}
