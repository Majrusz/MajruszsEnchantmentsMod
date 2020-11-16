package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.EnchantmentTypes;
import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
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

@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends Enchantment {
	public ImmortalityEnchantment() {
		super( Rarity.RARE, EnchantmentTypes.SHIELD, EquipmentSlotTypes.BOTH_HANDS );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 20 + ( ConfigHandler.Values.IMMORTALITY.get() ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		LivingEntity target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) <= 0.0F ) {
			ItemStack itemStack1 = target.getHeldItemMainhand(), itemStack2 = target.getHeldItemOffhand();

			if( itemStack1.getItem() instanceof ShieldItem ) {
				if( tryCheatDeath( target, itemStack1 ) )
					event.setCanceled( true );
			} else if( itemStack2.getItem() instanceof ShieldItem ) {
				if( tryCheatDeath( target, itemStack2 ) )
					event.setCanceled( true );
			}
		}
	}

	private static boolean tryCheatDeath( LivingEntity target, ItemStack itemStack ) {
		if( EnchantmentHelper.getEnchantmentLevel( RegistryHandler.IMMORTALITY.get(), itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			ServerWorld world = ( ServerWorld )target.getEntityWorld();
			world.spawnParticle( ParticleTypes.TOTEM_OF_UNDYING, target.getPosX(), target.getPosYHeight( 0.75D ), target.getPosZ(), 64, 0.25D, 0.5D, 0.25D, 0.5D );
			world.playSound( null, target.getPosX(), target.getPosY(), target.getPosZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.AMBIENT, 1.0F, 1.0F );

			itemStack.damageItem( 999999, target, ( entity )->entity.sendBreakAnimation( EquipmentSlotType.OFFHAND ) );

			return true;
		}

		return false;
	}
}
