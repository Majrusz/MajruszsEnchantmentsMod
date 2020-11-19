package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentUntouching;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class SmelterEnchantment extends Enchantment {
	public SmelterEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{ EntityEquipmentSlot.MAINHAND } );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 8 * ( level ) + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 16;
	}

	@Override
	public boolean canApplyTogether( Enchantment enchantment ) {
		return !( enchantment instanceof EnchantmentUntouching ) && super.canApplyTogether( enchantment );
	}

	@SubscribeEvent
	public static void onBlockDestroy( BlockEvent.HarvestDropsEvent event ) {
		if( !( event.getWorld() instanceof WorldServer && event.getHarvester() != null ) )
			return;

		int smelterLevel = EnchantmentHelper.getEnchantmentLevel( RegistryHandler.SMELTER, event.getHarvester().getActiveItemStack() );
		WorldServer world = ( WorldServer )event.getWorld();

		List< ItemStack > output = new ArrayList<>();
		for( ItemStack itemStack : event.getDrops() ) {
			if( WonderfulEnchantments.RANDOM.nextDouble() <= getSmeltChance( smelterLevel ) ) {
				ItemStack recipeOutput = FurnaceRecipes.instance().getSmeltingResult( itemStack );

				if( !recipeOutput.getDisplayName().matches( "Air" ) ) {
					output.add( recipeOutput );

					BlockPos position = event.getPos();
					int experience = ( 0.1f > WonderfulEnchantments.RANDOM.nextFloat() ? 1 : 0 );

					if( experience > 0 )
						world.spawnEntity( new EntityXPOrb( world, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, experience ) );
					world.spawnParticle( EnumParticleTypes.FLAME, position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D, 1 + WonderfulEnchantments.RANDOM.nextInt( 3 ), 0.125D, 0.125D, 0.125D, 0.03125D );
				} else
					output.add( itemStack );
			} else
				output.add( itemStack );
		}

		event.getDrops().clear();

		for( ItemStack itemStack : output )
			event.getDrops().add( itemStack );
	}

	protected static double getSmeltChance( int smelterLevel ) {
		switch( smelterLevel ) {
			case 1:
				return 0.25D;
			case 2:
				return 0.5D;
			case 3:
				return 1.0D;
			default:
				return 0.0D;
		}
	}
}
