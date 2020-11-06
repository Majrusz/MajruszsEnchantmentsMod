package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends DamageEnchantment {
    public HumanSlayerEnchantment() {
        super( Rarity.UNCOMMON, 3, EquipmentSlotType.MAINHAND );
    }

    @Override
    public int getMinEnchantability( int enchantmentLevel ) {
        return 5 + ( enchantmentLevel - 1 ) * 8;
    }

    @Override
    public int getMaxEnchantability( int enchantmentLevel ) {
        return this.getMinEnchantability( enchantmentLevel ) + 20;
    }

    @Override
    public float calcDamageByCreature( int level, CreatureAttribute creatureType ) {
        return 0.0F;
    }

    @Override
    public boolean canApply( ItemStack stack ) {
        return stack.getItem() instanceof AxeItem ? true : super.canApply( stack );
    }

    @SubscribeEvent
    public static void onEntityHurt( LivingHurtEvent event ) {
        Entity damageSource = event.getSource().getImmediateSource();

        if( damageSource instanceof LivingEntity ) {
            LivingEntity entity = event.getEntityLiving(), entitySource = ( LivingEntity )damageSource;

            float extraDamage = ( float )Math.floor( 2.0F * EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.HUMAN_SLAYER.get(), entitySource ) );

            if( isHuman( entity ) && extraDamage > 0.0F ) {
                ( ( ServerWorld )entitySource.getEntityWorld() ).spawnParticle( ParticleTypes.ENCHANTED_HIT, entity.getPosX(), entity.getPosYHeight( 0.625D ), entity.getPosZ(), 24, 0.125D, 0.25D, 0.125D, 0.5D );
                event.setAmount( extraDamage + event.getAmount() );
            }
        }
    }

    private static boolean isHuman( Entity entity ) {
        return ( entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity || entity instanceof PlayerEntity || entity instanceof WitchEntity || entity instanceof AbstractIllagerEntity );
    }
}