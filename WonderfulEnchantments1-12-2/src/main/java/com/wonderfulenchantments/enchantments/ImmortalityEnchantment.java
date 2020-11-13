package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends Enchantment {
	public ImmortalityEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 20;
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		EntityLivingBase target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) <= 0.0F ) {
			ItemStack itemStack1 = target.getHeldItemMainhand(), itemStack2 = target.getHeldItemOffhand();

			if( itemStack1.getItem() instanceof ItemShield ) {
				if( tryCheatDeath( target, itemStack1 ) )
					event.setCanceled( true );
			} else if( itemStack2.getItem() instanceof ItemShield ) {
				if( tryCheatDeath( target, itemStack2 ) )
					event.setCanceled( true );
			}
		}
	}

	private static boolean tryCheatDeath( EntityLivingBase target, ItemStack itemStack ) {
		if( EnchantmentHelper.getEnchantmentLevel( RegistryHandler.IMMORTALITY, itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			WorldServer world = ( WorldServer )target.getEntityWorld();
			world.spawnParticle( EnumParticleTypes.TOTEM, target.posX, target.posY + target.height * 0.75D, target.posZ, 64, 0.25D, 0.5D, 0.25D, 0.5D );
			world.playSound( null, target.posX, target.posY, target.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.AMBIENT, 1.0F, 1.0F );

			itemStack.damageItem( 999999, target );

			return true;
		}

		return false;
	}
}
