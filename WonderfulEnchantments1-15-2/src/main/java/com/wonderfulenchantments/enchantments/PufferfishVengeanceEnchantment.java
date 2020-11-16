package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PufferfishVengeanceEnchantment extends Enchantment {
	public PufferfishVengeanceEnchantment() {
		super( Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{ EquipmentSlotType.MAINHAND } );
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 5 + enchantmentLevel * 12 + ( ConfigHandler.Values.PUFFERFISH_VENGEANCE.get() ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof AxeItem ? true : super.canApply( stack );
	}

	@SubscribeEvent
	public static void onHit( LivingAttackEvent event ) {
		Entity entitySource = event.getSource().getImmediateSource();
		if( entitySource instanceof LivingEntity ) {
			LivingEntity attacker = ( LivingEntity )entitySource;
			int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.PUFFERFISH_VENGEANCE.get(), attacker );

			if( enchantmentLevel > 0 ) {
				LivingEntity target = event.getEntityLiving();

				int duration = 20 * ( 2 * enchantmentLevel + 1 );

				target.addPotionEffect( new EffectInstance( Effects.HUNGER, duration, 2 ) );
				target.addPotionEffect( new EffectInstance( Effects.POISON, duration, 3 ) );
				target.addPotionEffect( new EffectInstance( Effects.NAUSEA, duration, 0 ) );

				World world = attacker.getEntityWorld();
				if( world instanceof ServerWorld )
					( ( ServerWorld )attacker.getEntityWorld() ).playSound( null, target.getPosX(), target.getPosY(), target.getPosZ(), SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.AMBIENT, 1.0F, 1.0F );
			}
		}
	}
}
