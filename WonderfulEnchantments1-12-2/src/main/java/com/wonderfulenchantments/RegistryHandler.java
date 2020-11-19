package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.enchantments.*;
import com.wonderfulenchantments.items.ShieldReplacementItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber( modid = WonderfulEnchantments.MOD_ID )
public class RegistryHandler {
    public static final List< Enchantment > ENCHANTMENTS = new ArrayList<>();
    public static final List< Item > ITEMS = new ArrayList<>();

    public static final Enchantment
        FISHING_FANATIC         = new FanaticEnchantment( "fishing_fanatic" ),
        HUMAN_SLAYER            = new HumanSlayerEnchantment( "human_slayer" ),
        DODGE                   = new DodgeEnchantment( "dodge" ),
        ENLIGHTENMENT           = new EnlightenmentEnchantment( "enlightenment" ),
        VITALITY                = new VitalityEnchantment( "vitality" ),
        PHOENIX_DIVE            = new PhoenixDiveEnchantment( "phoenix_dive" ),
        PUFFERFISH_VENGEANCE    = new PufferfishVengeanceEnchantment( "pufferfish_vengeance" ),
        IMMORTALITY             = new ImmortalityEnchantment( "immortality" ),
        SMELTER                 = new SmelterEnchantment( "smelter" );

    public static final Enchantment
        SLOWNESS                = new SlownessCurse( "slowness_curse" ),
        FATIGUE                 = new FatigueCurse( "fatigue_curse" );

    public static final Item
        SHIELD_REPLACEMENT      = new ShieldReplacementItem( "shield" );

    @SubscribeEvent
    public static void onEnchantmentRegister( RegistryEvent.Register< Enchantment > event ) {
        event.getRegistry().registerAll( ENCHANTMENTS.toArray( new Enchantment[ 0 ] ) );
    }

    @SubscribeEvent
    public static void onItemRegister( RegistryEvent.Register< Item > event ) {
        event.getRegistry().registerAll( ITEMS.toArray( new Item[ 0 ] ) );
    }
}
