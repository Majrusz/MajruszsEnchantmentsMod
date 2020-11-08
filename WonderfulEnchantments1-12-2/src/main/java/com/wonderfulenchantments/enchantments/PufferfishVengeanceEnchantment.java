package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PufferfishVengeanceEnchantment extends Enchantment {
	public PufferfishVengeanceEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 5 + enchantmentLevel * 12;
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof ItemAxe ? true : super.canApply( stack );
	}

	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		Entity entitySource = event.getSource().getImmediateSource();
		if( entitySource instanceof EntityLivingBase ) {
			EntityLivingBase attacker = ( EntityLivingBase )entitySource;
			int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.PUFFERFISH_VENGEANCE, attacker );

			if( enchantmentLevel > 0 ) {
				EntityLivingBase target = event.getEntityLiving();
				int duration = 20 * ( 2 * enchantmentLevel + 1 );

				target.addPotionEffect( new PotionEffect( MobEffects.HUNGER, duration, 2 ) );
				target.addPotionEffect( new PotionEffect( MobEffects.POISON, duration, 3 ) );
				target.addPotionEffect( new PotionEffect( MobEffects.NAUSEA, duration, 0 ) );
			}
		}
	}
}
