package com.wonderfulenchantments;

import com.wonderfulenchantments.enchantments.*;
import com.wonderfulenchantments.items.ShieldReplacementItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister< Enchantment > ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS, WonderfulEnchantments.MODID );
    public static final DeferredRegister< Item > ITEMS_TO_REPLACE = DeferredRegister.create( ForgeRegistries.ITEMS, "minecraft" );

    public static class EnchantmentTypes {
        public static final EnchantmentType SHIELD = EnchantmentType.create( "shield", ( Item itemIn ) -> { return itemIn instanceof ShieldItem; } );

        public static void addTypeToItemGroup( EnchantmentType type, ItemGroup itemGroup ) {
            EnchantmentType[] group = itemGroup.getRelevantEnchantmentTypes();
            EnchantmentType[] temporary = new EnchantmentType[ group.length+1 ];
            System.arraycopy( group, 0, temporary, 0, group.length );
            temporary[ group.length-1 ] = type;

            itemGroup.setRelevantEnchantmentTypes( temporary );
        }
    }

    public static void init() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENCHANTMENTS.register( modEventBus );
        ITEMS_TO_REPLACE.register( modEventBus );

        EnchantmentTypes.addTypeToItemGroup( EnchantmentTypes.SHIELD, ItemGroup.COMBAT );
    }

    // Enchantments
    public static final RegistryObject< Enchantment >
        FISHING_FANATIC     = ENCHANTMENTS.register( "fishing_fanatic", FanaticEnchantment::new ),
        HUMAN_SLAYER        = ENCHANTMENTS.register( "human_slayer", HumanSlayerEnchantment::new ),
        DODGE               = ENCHANTMENTS.register( "dodge", DodgeEnchantment::new ),
        ENLIGHTENMENT       = ENCHANTMENTS.register( "enlightenment", EnlightenmentEnchantment::new ),
        VITALITY            = ENCHANTMENTS.register( "vitality", VitalityEnchantment::new );

    // Items to replace
    public static final RegistryObject< Item >
        SHIELD_REPLACEMENT = ITEMS_TO_REPLACE.register( "shield", ShieldReplacementItem::new );
}
