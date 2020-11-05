package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnlightenmentEnchantment extends Enchantment {
    public EnlightenmentEnchantment( String name ) {
        super( Rarity.RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{ EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET } );

        this.setName( name );
        this.setRegistryName( WonderfulEnchantments.MODID + ":" + name );
        RegistryHandler.ENCHANTMENTS.add( this );
    }

    @Override
    public int getMinEnchantability( int enchantmentLevel ) {
        return 6 + enchantmentLevel * 12;
    }

    @Override
    public int getMaxEnchantability( int enchantmentLevel ) {
        return this.getMinEnchantability( enchantmentLevel ) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canApply( ItemStack stack ) {
        return super.canApply( stack );
    }

    @SubscribeEvent
    public static void onXPPickUp( PlayerPickupXpEvent event ) {
        int levelSum = 0;
        for( ItemStack armor : event.getEntityPlayer().getArmorInventoryList() )
            levelSum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.ENLIGHTENMENT, armor );

        if( levelSum > 0 ) {
            double bonusRatio = 0.25D * (double) levelSum;
            double randomBonus = bonusRatio * WonderfulEnchantments.RANDOM.nextDouble();
            int bonusExp = (int) (Math.round(randomBonus * (double) event.getOrb().getXpValue()));

            event.getEntityPlayer().addExperience( bonusExp );
        }
    }
}