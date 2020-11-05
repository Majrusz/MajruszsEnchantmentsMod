package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends EnchantmentDamage {
    public HumanSlayerEnchantment( String name ) {
        super( Rarity.UNCOMMON, 3, EntityEquipmentSlot.MAINHAND );

        this.setName( name );
        this.setRegistryName( WonderfulEnchantments.MODID, name );
        RegistryHandler.ENCHANTMENTS.add( this );
    }

    @Override
    public int getMinEnchantability( int enchantmentLevel ) {
        return 5 + ( enchantmentLevel-1 ) * 8;
    }

    @Override
    public int getMaxEnchantability( int enchantmentLevel ) {
        return this.getMinEnchantability( enchantmentLevel ) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float calcDamageByCreature( int level, EnumCreatureAttribute creatureType ) {
        return 0.0F;
    }

    @Override
    public boolean canApplyTogether( Enchantment enchantment ) {
        return !( enchantment instanceof EnchantmentDamage );
    }

    @Override
    public boolean canApply( ItemStack stack ) {
        return stack.getItem() instanceof ItemAxe ? true : super.canApply( stack );
    }

    @Override
    public String getName() {
        return new TextComponentTranslation( "enchantment.human_slayer"  ).getUnformattedComponentText();
    }

    @SubscribeEvent
    public static void onEntityHurt( LivingHurtEvent event ) {
        Entity damageSource = event.getSource().getImmediateSource();

        if( damageSource instanceof EntityPlayer ) {
            Entity entity = event.getEntityLiving();
            EntityPlayer entitySource = (EntityPlayer) damageSource;
            int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.HUMAN_SLAYER, entitySource );
            float extraDamage = ( float )Math.floor( enchantmentLevel * 2.0D );

            if((entity instanceof EntityVillager ||
                entity instanceof EntityPlayer ||
                entity instanceof EntityWitch) && enchantmentLevel > 0 ) {

                ( (WorldServer) entitySource.getEntityWorld() ).spawnParticle(
                    EnumParticleTypes.CRIT_MAGIC,
                    entity.posX, entity.posY+entity.height*( 0.625D ), entity.posZ,
                    24,
                    0.125D, 0.25D, 0.125D,
                    0.5D
                );
                event.setAmount( extraDamage + event.getAmount() );
            }
        }
    }
}
