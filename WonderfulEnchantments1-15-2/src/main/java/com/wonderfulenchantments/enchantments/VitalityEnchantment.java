package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
    public VitalityEnchantment() {
        super( Rarity.RARE, RegistryHandler.EnchantmentTypes.SHIELD, new EquipmentSlotType[]{ EquipmentSlotType.OFFHAND } );
    }

    @Override
    public int getMinEnchantability( int level ) {
        return 5 + 8 * ( level );
    }

    @Override
    public int getMaxEnchantability( int level ) {
        return this.getMinEnchantability( level ) + 10;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean canApplyTogether( Enchantment enchant ) {
        return super.canApplyTogether( enchant );
    }

    @SubscribeEvent
    public static void checkIfPlayerHasShield( TickEvent.PlayerTickEvent event ) {
        PlayerEntity player = event.player;
        String nickname = player.getDisplayName().getString();

        if( !bonuses.containsKey( nickname ) )
            VitalityEnchantment.bonuses.put( nickname, 0 );

        int totalVitality = getVitalityBonus( player );
        int currentMaxHealthBonus = getMaxHealthBonus( nickname );

        if( totalVitality != currentMaxHealthBonus ) {
            setMaxHealthBonus( nickname, totalVitality );
            updateMaxHealthBonus( player );
        }
    }

    private static int getVitalityBonus( PlayerEntity player ) {
        int sum = 0;

        ItemStack   item1 = player.getHeldItemMainhand(),
                    item2 = player.getHeldItemOffhand();

        if( item1.getItem() instanceof ShieldItem )
            sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY.get(), item1 );

        if( item2.getItem() instanceof ShieldItem )
            sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY.get(), item2 );

        return sum;
    }

    protected static HashMap< String, Integer > bonuses = new HashMap<>();
    protected static final UUID MODIFIER_UUID = UUID.fromString( "575cb29a-1ee4-11eb-adc1-0242ac120002" );
    protected static final String MODIFIER_NAME = "VitalityBonus";
    private static void updateMaxHealthBonus( PlayerEntity player ) {
        String nickname = player.getDisplayName().getString();
        IAttributeInstance maxHealth = player.getAttribute( SharedMonsterAttributes.MAX_HEALTH );

        maxHealth.removeModifier( MODIFIER_UUID );
        AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, 2*getMaxHealthBonus( nickname ), AttributeModifier.Operation.ADDITION );
        maxHealth.applyModifier( modifier );
    }

    private static void setMaxHealthBonus( String nickname, int value ) {
        VitalityEnchantment.bonuses.replace( nickname, value );
    }

    private static int getMaxHealthBonus( String nickname ) {
        return VitalityEnchantment.bonuses.get( nickname );
    }
}
