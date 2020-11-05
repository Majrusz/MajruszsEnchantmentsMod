package com.wonderfulenchantments.items;

import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;

public class ShieldReplacementItem extends ItemShield {
    public ShieldReplacementItem( String name ) {
        super();

        this.setUnlocalizedName( name );
        this.setRegistryName( "minecraft", name );
        this.setCreativeTab( CreativeTabs.COMBAT );

        RegistryHandler.ITEMS.add( this );
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}