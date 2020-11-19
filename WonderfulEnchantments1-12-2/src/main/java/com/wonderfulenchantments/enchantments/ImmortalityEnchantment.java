package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.EquipmentSlotTypes;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantmentHelper;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ImmortalityEnchantment extends Enchantment {
	protected static final int damageOnUse = 9001;

	public ImmortalityEnchantment( String name ) {
		super( Rarity.RARE, EnumEnchantmentType.BREAKABLE, EquipmentSlotTypes.BOTH_HANDS );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability( int level ) {
		return 20 + WonderfulEnchantmentHelper.increaseLevelIfEnchantmentIsDisabled( this );
	}

	@Override
	public int getMaxEnchantability( int level ) {
		return this.getMinEnchantability( level ) + 30;
	}

	@Override
	public boolean canApplyAtEnchantingTable( ItemStack stack ) {
		return ( stack.getItem() instanceof ItemShield ) && super.canApplyAtEnchantingTable( stack );
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		EntityLivingBase target = event.getEntityLiving();

		if( ( target.getHealth() - event.getAmount() ) <= 0.0F ) {

			if( tryCheatDeath( target, target.getHeldItemMainhand() ) )
				event.setCanceled( true );
			else if( tryCheatDeath( target, target.getHeldItemOffhand() ) )
				event.setCanceled( true );
		}
	}

	protected static boolean tryCheatDeath( EntityLivingBase target, ItemStack itemStack ) {
		if( itemStack.getItem() instanceof ItemShield && EnchantmentHelper.getEnchantmentLevel( RegistryHandler.IMMORTALITY, itemStack ) > 0 ) {
			target.setHealth( target.getMaxHealth() );

			spawnParticlesAndPlaySounds( target );
			itemStack.damageItem( damageOnUse, target );

			return true;
		}

		return false;
	}

	protected static void spawnParticlesAndPlaySounds( EntityLivingBase entityLivingBase ) {
		WorldServer world = ( WorldServer )entityLivingBase.getEntityWorld();
		world.spawnParticle( EnumParticleTypes.TOTEM, entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.height * 0.75D, entityLivingBase.posZ, 64, 0.25D, 0.5D, 0.25D, 0.5D );
		world.playSound( null, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.AMBIENT, 1.0F, 1.0F );
	}
}