package com.wonderfulenchantments.enchantments;

import com.wonderfulenchantments.ConfigHandler;
import com.wonderfulenchantments.RegistryHandler;
import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class HumanSlayerEnchantment extends EnchantmentDamage {
	public HumanSlayerEnchantment( String name ) {
		super( Rarity.UNCOMMON, 3, EntityEquipmentSlot.MAINHAND );

		this.setName( name );
		this.setRegistryName( WonderfulEnchantments.MOD_ID, name );
		RegistryHandler.ENCHANTMENTS.add( this );
	}

	@Override
	public int getMinEnchantability( int enchantmentLevel ) {
		return 5 + ( enchantmentLevel - 1 ) * 8 + ( ConfigHandler.Enchantments.HUMAN_SLAYER ? 0 : RegistryHandler.disableEnchantmentValue );
	}

	@Override
	public int getMaxEnchantability( int enchantmentLevel ) {
		return this.getMinEnchantability( enchantmentLevel ) + 20;
	}

	@Override
	public float calcDamageByCreature( int level, EnumCreatureAttribute creatureType ) {
		return 0.0F;
	}

	@Override
	public String getName() {
		return new TextComponentTranslation( "enchantment.human_slayer" ).getUnformattedComponentText();
	}

	@Override
	public boolean canApply( ItemStack stack ) {
		return stack.getItem() instanceof ItemAxe ? true : super.canApply( stack );
	}

	@SubscribeEvent
	public static void onEntityHurt( LivingHurtEvent event ) {
		Entity entitySource = event.getSource().getImmediateSource();

		if( entitySource instanceof EntityPlayer ) {
			EntityLivingBase target = event.getEntityLiving();

			EntityPlayer attacker = ( EntityPlayer )entitySource;
			int enchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel( RegistryHandler.HUMAN_SLAYER, attacker );
			float extraDamage = ( float )Math.floor( enchantmentLevel * 2.0D );

			if( isHuman( target ) && enchantmentLevel > 0 ) {
				( ( WorldServer )attacker.getEntityWorld() ).spawnParticle( EnumParticleTypes.CRIT_MAGIC, target.posX, target.posY + target.height * ( 0.625D ), target.posZ, 24, 0.125D, 0.25D, 0.125D, 0.5D );
				event.setAmount( extraDamage + event.getAmount() );
			}
		}
	}

	private static boolean isHuman( Entity entity ) {
		return ( entity instanceof EntityVillager || entity instanceof EntityPlayer || entity instanceof EntityWitch );
	}
}
