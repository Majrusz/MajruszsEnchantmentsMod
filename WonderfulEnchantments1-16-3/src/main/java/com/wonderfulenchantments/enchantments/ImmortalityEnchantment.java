package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
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

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled;

@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends Enchantment {
	protected static final int damageOnUse = 9001;

	public ImmortalityEnchantment() {
		super( Rarity.RARE, WonderfulEnchantmentHelper.SHIELD, EquipmentSlotTypes.BOTH_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 20 + increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) < 1.0F ) {
			if( tryCheatDeath( target, target.getHeldItemMainhand() ) )
				event.setCanceled( true );
			else if( tryCheatDeath( target, target.getHeldItemOffhand() ) )
				event.setCanceled( true );
		}
	}

	protected static boolean tryCheatDeath( LivingEntity target, ItemStack itemStack ) {
		if( itemStack.getItem() instanceof ShieldItem && EnchantmentHelper.getEnchantmentLevel( RegistryHandler.IMMORTALITY.get(), itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			spawnParticlesAndPlaySounds( target );
			itemStack.damageItem( damageOnUse, target, ( entity )->entity.sendBreakAnimation( EquipmentSlotType.OFFHAND ) );

			return true;
		}

		return false;
	}

	protected static void spawnParticlesAndPlaySounds( LivingEntity livingEntity ) {
		ServerWorld world = ( ServerWorld )livingEntity.getEntityWorld();
		world.spawnParticle( ParticleTypes.TOTEM_OF_UNDYING, livingEntity.getPosX(), livingEntity.getPosYHeight( 0.75D ), livingEntity.getPosZ(), 64,
			0.25D, 0.5D, 0.25D, 0.5D
		);
		world.playSound( null, livingEntity.getPosX(), livingEntity.getPosY(), livingEntity.getPosZ(), SoundEvents.ITEM_TOTEM_USE,
			SoundCategory.AMBIENT, 1.0F, 1.0F
		);
	}
}
