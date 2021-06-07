package com.wonderfulenchantments;

import com.wonderfulenchantments.renderers.HorseRendererReplacement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static void setup() {
		EntityRendererManager rendererManager = Minecraft.getInstance()
			.getRenderManager();
		rendererManager.register( EntityType.HORSE, new HorseRendererReplacement( rendererManager ) );

		if( Instances.CLIENT_EFFECTS.isEnchantedBookTextureReplacementEnabled() )
			ItemModelsProperties.registerProperty( Items.ENCHANTED_BOOK, new ResourceLocation( "book_type" ),
				RegistryHandlerClient::enchantmentBookPredicate
			);
	}

	/** Checks whether given item stack has enchantments from Wonderful Enchantments mod. */
	private static float enchantmentBookPredicate( ItemStack itemStack, ClientWorld clientWorld, LivingEntity entity ) {
		Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( itemStack );

		for( Map.Entry< Enchantment, Integer > enchantmentPair : enchantments.entrySet() ) {
			Enchantment enchantment = enchantmentPair.getKey();
			ResourceLocation enchantmentLocation = enchantment.getRegistryName();
			if( enchantmentLocation == null )
				continue;

			String enchantmentName = enchantmentLocation.getNamespace();
			if( enchantmentName.contains( "wonderful_enchantments" ) )
				return 1.0f;
		}

		return 0.0f;
	}
}
