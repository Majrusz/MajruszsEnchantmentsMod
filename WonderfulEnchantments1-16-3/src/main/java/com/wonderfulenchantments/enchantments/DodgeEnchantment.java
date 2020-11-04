package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DodgeEnchantment extends Enchantment {
    public DodgeEnchantment() {
        super( Rarity.RARE, EnchantmentType.ARMOR_LEGS, new EquipmentSlotType[]{ EquipmentSlotType.LEGS } );
    }

    @Override
    public int getMinEnchantability( int level ) {
        return 14 * ( level );
    }

    @Override
    public int getMaxEnchantability( int level ) {
        return this.getMinEnchantability( level ) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean canApplyTogether( Enchantment enchant ) {
        return super.canApplyTogether( enchant );
    }

    @SubscribeEvent
    public static void onEntityHurt( LivingDamageEvent event ) {
        LivingEntity entity = event.getEntityLiving();
        int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.DODGE.get(), entity );

        if( enchantmentLevel > 0 ) {
            if( !( WonderfulEnchantments.RANDOM.nextDouble() < ( double )enchantmentLevel * 0.125D ) )
                return;

            for( double d = 0.0D; d < 3.0D; d++ ) {
                ((ServerWorld) entity.getEntityWorld()).spawnParticle(
                    ParticleTypes.SMOKE,
                    entity.getPosX(), entity.getPosYHeight( 0.25D*( d+1.0D ) ), entity.getPosZ(),
                    32,
                    0.125D, 0.0D, 0.125D,
                    0.075D
                );
                ((ServerWorld) entity.getEntityWorld()).spawnParticle(
                    ParticleTypes.LARGE_SMOKE,
                    entity.getPosX(), entity.getPosYHeight( 0.25D*( d+1.0D ) ), entity.getPosZ(),
                    16,
                    0.125D, 0.0D, 0.125D,
                    0.025D
                );
            }

            for( ItemStack itemstack : entity.getArmorInventoryList() ) {
                ListNBT nbt = itemstack.getEnchantmentTagList();

                for( int i = 0; i < nbt.size(); ++i )
                    if( nbt.getCompound( i ).getString( "id" ).contains( "wonderful_enchantments:dodge" ) ) {
                        itemstack.damageItem( ( int )event.getAmount(), entity, ( e ) -> {
                            e.sendBreakAnimation( EquipmentSlotType.LEGS );
                        } );
                        break;
                    }
            }

            event.setCanceled( true );
        }
    }
}
