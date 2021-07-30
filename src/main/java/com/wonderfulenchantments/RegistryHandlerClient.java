package com.wonderfulenchantments;

import com.wonderfulenchantments.renderers.HorseRendererReplacement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn( Dist.CLIENT )
public class RegistryHandlerClient {
	public static void setup() {
		EntityRenderers.register( EntityType.HORSE, HorseRendererReplacement::new );

		if( Instances.CLIENT_EFFECTS.isEnchantedBookTextureReplacementEnabled() )
			ItemProperties.register( Items.ENCHANTED_BOOK, new ResourceLocation( "book_type" ), RegistryHandlerClient::enchantmentBookPredicate );
	}

	/** Checks whether given item stack has enchantments from Wonderful Enchantments mod. */
	private static float enchantmentBookPredicate( ItemStack itemStack, ClientLevel clientWorld, LivingEntity entity, int i ) {
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
