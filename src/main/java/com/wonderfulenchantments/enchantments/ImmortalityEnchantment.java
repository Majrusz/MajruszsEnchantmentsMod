package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlots;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that causes shield to work like Totem of Undying. */
@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends WonderfulEnchantment {
	protected static final int DAMAGE_ON_USE = 9001;

	public ImmortalityEnchantment() {
		super( "immortality", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlots.BOTH_HANDS, "Immortality" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->20 );
	}

	/** Event on which enchantment effect is applied if it is possible. */
	@SubscribeEvent
	public static void onEntityHurt( LivingDamageEvent event ) {
		LivingEntity target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) < 1.0f ) {
			if( tryCheatDeath( target, EquipmentSlot.MAINHAND ) )
				event.setCanceled( true );
			else if( tryCheatDeath( target, EquipmentSlot.OFFHAND ) )
				event.setCanceled( true );
		}
	}

	/**
	 Cheats death when player holds shield and it has this enchantment.

	 @return Returns whether player successfully cheated death.
	 */
	protected static boolean tryCheatDeath( LivingEntity target, EquipmentSlot equipmentSlot ) {
		ItemStack itemStack = target.getItemBySlot( equipmentSlot );
		if( itemStack.getItem() instanceof ShieldItem && Instances.IMMORTALITY.hasEnchantment( itemStack ) ) {
			target.setHealth( target.getMaxHealth() );

			spawnParticlesAndPlaySounds( target );
			itemStack.hurtAndBreak( DAMAGE_ON_USE, target, ( entity )->entity.broadcastBreakEvent( equipmentSlot ) );

			return true;
		}

		return false;
	}

	/** Spawns particles and plays sound when cheating death. */
	protected static void spawnParticlesAndPlaySounds( LivingEntity livingEntity ) {
		ServerLevel world = ( ServerLevel )livingEntity.level;
		world.sendParticles( ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getX(), livingEntity.getY( 0.75 ), livingEntity.getZ(), 64,
			0.25, 0.5, 0.25, 0.5
		);
		world.playSound( null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.TOTEM_USE,
			SoundSource.AMBIENT, 1.0f, 1.0f
		);
	}
}
