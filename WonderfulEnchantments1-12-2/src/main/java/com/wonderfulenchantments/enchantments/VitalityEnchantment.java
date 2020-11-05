package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class VitalityEnchantment extends Enchantment {
    public VitalityEnchantment( String name ) {
        super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND } );

        this.setName( name );
        this.setRegistryName( WonderfulEnchantments.MODID, name );
        RegistryHandler.ENCHANTMENTS.add( this );
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

    @Override
    public boolean canApply( ItemStack stack ) {
        return super.canApply( stack ) && ( stack.getItem() instanceof ItemShield );
    }

    @SubscribeEvent
    public static void checkIfPlayerHasShield( TickEvent.PlayerTickEvent event ) {
        EntityPlayer player = event.player;
        String nickname = player.getDisplayName().getUnformattedText();

        if( !bonuses.containsKey( nickname ) )
            VitalityEnchantment.bonuses.put( nickname, 0 );

        int totalVitality = getVitalityBonus( player );
        int currentMaxHealthBonus = getMaxHealthBonus( nickname );

        if( totalVitality != currentMaxHealthBonus ) {
            setMaxHealthBonus( nickname, totalVitality );
            updateMaxHealthBonus( player );
        }
    }

    private static int getVitalityBonus( EntityPlayer player ) {
        int sum = 0;

        ItemStack   item1 = player.getHeldItemMainhand(),
                    item2 = player.getHeldItemOffhand();

        if( item1.getItem() instanceof ItemShield )
            sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY, item1 );

        if( item2.getItem() instanceof ItemShield )
            sum += EnchantmentHelper.getEnchantmentLevel( RegistryHandler.VITALITY, item2 );

        return sum;
    }

    protected static HashMap< String, Integer > bonuses = new HashMap<>();
    protected static final UUID MODIFIER_UUID = UUID.fromString( "575cb29a-1ee4-11eb-adc1-0242ac120002" );
    protected static final String MODIFIER_NAME = "VitalityBonus";
    private static void updateMaxHealthBonus( EntityPlayer player ) {
        String nickname = player.getDisplayName().getUnformattedText();
        IAttributeInstance maxHealth = player.getEntityAttribute( SharedMonsterAttributes.MAX_HEALTH );

        maxHealth.removeModifier( MODIFIER_UUID );
        AttributeModifier modifier = new AttributeModifier( MODIFIER_UUID, MODIFIER_NAME, 2*getMaxHealthBonus( nickname ), Constants.AttributeModifierOperation.ADD );
        maxHealth.applyModifier( modifier );
    }

    private static void setMaxHealthBonus( String nickname, int value ) {
        VitalityEnchantment.bonuses.replace( nickname, value );
    }

    private static int getMaxHealthBonus( String nickname ) {
        return VitalityEnchantment.bonuses.get( nickname );
    }
}
