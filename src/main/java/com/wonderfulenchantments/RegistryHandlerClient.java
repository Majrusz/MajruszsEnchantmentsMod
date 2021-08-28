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

		ClientEffects clientEffects = Instances.CLIENT_EFFECTS;
		if( clientEffects.isEnchantedBookTextureReplacementEnabled() || clientEffects.isCombinedBookTextureReplacementEnabled() )
			ItemProperties.register( Items.ENCHANTED_BOOK, new ResourceLocation( "book_type" ), RegistryHandlerClient::enchantmentBookPredicate );
	}

	/** Checks for different enchantments on enchanted book to determine witch texture should be used. */
	private static float enchantmentBookPredicate( ItemStack itemStack, ClientLevel clientWorld, LivingEntity entity, int i ) {
		Map< Enchantment, Integer > enchantments = EnchantmentHelper.getEnchantments( itemStack );

		boolean hasWonderfulEnchantments = false;
		boolean hasOtherEnchantments = false;
		for( Map.Entry< Enchantment, Integer > enchantmentPair : enchantments.entrySet() ) {
			Enchantment enchantment = enchantmentPair.getKey();
			ResourceLocation enchantmentLocation = enchantment.getRegistryName();
			if( enchantmentLocation == null )
				continue;

			String enchantmentName = enchantmentLocation.getNamespace();
			if( enchantmentName.contains( "wonderful_enchantments" ) ) {
				hasWonderfulEnchantments = true;
			} else {
				hasOtherEnchantments = true;
			}

			if( hasWonderfulEnchantments && hasOtherEnchantments )
				break;
		}

		ClientEffects clientEffects = Instances.CLIENT_EFFECTS;
		hasWonderfulEnchantments = hasWonderfulEnchantments && clientEffects.isEnchantedBookTextureReplacementEnabled();
		if( hasWonderfulEnchantments && hasOtherEnchantments && clientEffects.isCombinedBookTextureReplacementEnabled() )
			return 2.0f;
		else if( hasWonderfulEnchantments )
			return 1.0f;

		return 0.0f;
	}
}
