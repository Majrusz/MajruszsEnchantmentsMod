package com.majruszsenchantments;

import com.mlib.Utility;
import com.mlib.config.BooleanConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class RegistriesClient {
	static BooleanConfig UNIQUE_BOOK_TEXTURE;

	public static void initialize() {
		UNIQUE_BOOK_TEXTURE = new BooleanConfig( true );

		MajruszsEnchantments.CLIENT_CONFIG.addConfig( UNIQUE_BOOK_TEXTURE.name( "unique_book_texture" )
			.comment( "Should the Enchanted Book has a different texture when it has any of the new enchantments on it (disabling it may fix some bugs with other mods)?" )
			.requiresWorldRestart( true )
		);
		FMLJavaModLoadingContext.get().getModEventBus().addListener( RegistriesClient::register );
	}

	private static void register( final FMLClientSetupEvent event ) {
		if( UNIQUE_BOOK_TEXTURE.isEnabled() ) {
			ItemProperties.register( Items.ENCHANTED_BOOK, new ResourceLocation( "book_type" ), RegistriesClient::enchantmentBookPredicate );
		}
	}

	private static float enchantmentBookPredicate( ItemStack itemStack, ClientLevel clientWorld, LivingEntity entity, int i ) {
		Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( itemStack );

		boolean hasWonderfulEnchantments = false;
		boolean hasOtherEnchantments = false;
		for( Map.Entry< Enchantment, Integer > enchantmentPair : enchantments.entrySet() ) {
			ResourceLocation enchantmentLocation = Utility.getRegistryKey( enchantmentPair.getKey() );
			if( enchantmentLocation == null )
				continue;

			String enchantmentName = enchantmentLocation.getNamespace();
			if( enchantmentName.contains( "majruszsenchantments" ) ) {
				hasWonderfulEnchantments = true;
			} else {
				hasOtherEnchantments = true;
			}

			if( hasWonderfulEnchantments && hasOtherEnchantments )
				break;
		}

		if( hasWonderfulEnchantments ) {
			return hasOtherEnchantments ? 2.0f : 1.0f;
		}

		return 0.0f;
	}
}
