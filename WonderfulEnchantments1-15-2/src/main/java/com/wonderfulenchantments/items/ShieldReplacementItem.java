package com.wonderfulenchantments.items;

import net.minecraft.item.*;

public class ShieldReplacementItem extends ShieldItem {
    public ShieldReplacementItem() {
        super( ( new Item.Properties() ).maxDamage( 336 ).group( ItemGroup.COMBAT ) );
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}