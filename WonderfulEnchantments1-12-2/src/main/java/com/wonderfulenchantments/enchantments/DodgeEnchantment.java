package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class DodgeEnchantment extends Enchantment {
    public DodgeEnchantment( String name ) {
        super( Rarity.RARE, EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{ EntityEquipmentSlot.LEGS } );

        this.setName( name );
        this.setRegistryName( WonderfulEnchantments.MODID, name );
        RegistryHandler.ENCHANTMENTS.add( this );
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
        EntityLivingBase entity = event.getEntityLiving();
        int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.DODGE, entity );

        if( enchantmentLevel > 0 ) {
            if( !( WonderfulEnchantments.RANDOM.nextDouble() < ( double )enchantmentLevel * 0.125D ) )
                return;

            for( double d = 0.0D; d < 3.0D; d++ ) {
                ((WorldServer) entity.getEntityWorld()).spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    entity.posX, entity.posY+entity.height*( 0.25D*( d+1.0D ) ), entity.posZ,
                    32,
                    0.125D, 0.0D, 0.125D,
                    0.075D
                );
                ((WorldServer) entity.getEntityWorld()).spawnParticle(
                    EnumParticleTypes.SMOKE_LARGE,
                    entity.posX, entity.posY+entity.height*( 0.25D*( d+1.0D ) ), entity.posZ,
                    16,
                    0.125D, 0.0D, 0.125D,
                    0.025D
                );
            }

            for( ItemStack armor : entity.getArmorInventoryList() ) {
                int level = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.DODGE, armor );

                if( level > 0 ) {
                    armor.damageItem( ( int )event.getAmount(), entity );
                    if( entity instanceof EntityPlayer )
                        setImmunity( (EntityPlayer)( entity ), 100 );
                    break;
                }
            }

            event.setCanceled( true );
        }
    }

    protected static HashMap< String, Integer > modifiers = new HashMap<>();
    protected static final UUID MODIFIER_UUID = UUID.fromString( "ad3e064e-e9f6-4747-a86b-46dc4e2a1444" );
    protected static final String MODIFIER_NAME = "KnockbackImmunityTime";
    private static void setImmunity( EntityPlayer player, int ticks ) {
        String nickname = player.getDisplayName().getUnformattedText();

        if( !modifiers.containsKey( nickname ) )
            modifiers.put( nickname, 0 );

        modifiers.replace( nickname, ticks );

        applyImmunity( player );
    }

    private static void applyImmunity( EntityPlayer player ) {
        String nickname = player.getDisplayName().getUnformattedText();

        IAttributeInstance resistance = player.getEntityAttribute( SharedMonsterAttributes.KNOCKBACK_RESISTANCE );
        resistance.removeModifier( MODIFIER_UUID );
        AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, ( modifiers.get( nickname ) > 0 )? 1.0D : 0.0D, Constants.AttributeModifierOperation.ADD );
        resistance.applyModifier( modifier );
    }

    @SubscribeEvent
    public static void checkPlayersKnockbackImmunity( TickEvent.PlayerTickEvent event ) {
        EntityPlayer player = event.player;
        String nickname = player.getDisplayName().getUnformattedText();

        if( !modifiers.containsKey( nickname ) )
            modifiers.put( nickname, 0 );

        applyImmunity( player );

        modifiers.replace( nickname, Math.max( modifiers.get( nickname )-1, 0 ) );
    }
}
