package com.wonderfulenchantments.enchantments;

import com.mlib.EquipmentSlotTypes;
import com.wonderfulenchantments.Instances;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Enchantment that causes shield to work like Totem of Undying. */
@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends WonderfulEnchantment {
	protected static final int DAMAGE_ON_USE = 9001;

	public ImmortalityEnchantment() {
		super( "immortality", Rarity.RARE, RegistryHandler.SHIELD, EquipmentSlotTypes.BOTH_HANDS, "Immortality" );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 30 );
		setMinimumEnchantabilityCalculator( level->20 );
	}

	/** Event on which enchantment effect is applied if it is possible. */
	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) < 1.0f ) {
			if( tryCheatDeath( target, target.getHeldItemMainhand() ) )
				event.setCanceled( true );
			else if( tryCheatDeath( target, target.getHeldItemOffhand() ) )
				event.setCanceled( true );
		}
	}

	/**
	 Cheating death when players is holding shield and it has this enchantment.

	 @param target    Entity which will receive full health on death.
	 @param itemStack Item stack to check.

	 @return Returns whether player successfully cheated death.
	 */
	protected static boolean tryCheatDeath( LivingEntity target, ItemStack itemStack ) {
		if( itemStack.getItem() instanceof ShieldItem && EnchantmentHelper.getEnchantmentLevel( Instances.IMMORTALITY, itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			spawnParticlesAndPlaySounds( target );
			itemStack.damageItem( DAMAGE_ON_USE, target, ( entity )->entity.sendBreakAnimation( EquipmentSlotType.OFFHAND ) );

			return true;
		}

		return false;
	}

	/**
	 Spawning particles and playing sound on cheating death.

	 @param livingEntity Entity where the effects will be generated.
	 */
	protected static void spawnParticlesAndPlaySounds( LivingEntity livingEntity ) {
		ServerWorld world = ( ServerWorld )livingEntity.getEntityWorld();
		world.spawnParticle( ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getPosX(), livingEntity.getPosYHeight( 0.75 ), livingEntity.getPosZ(), 64,
			0.25, 0.5, 0.25, 0.5
		);
		world.playSound( null, livingEntity.getPosX(), livingEntity.getPosY(), livingEntity.getPosZ(), SoundEvents.ITEM_TOTEM_USE,
			SoundCategory.AMBIENT, 1.0f, 1.0f
		);
	}
}
