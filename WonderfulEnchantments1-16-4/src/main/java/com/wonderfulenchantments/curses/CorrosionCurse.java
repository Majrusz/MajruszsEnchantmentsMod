package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonderfulenchantments.WonderfulEnchantmentHelper.isEntityOutsideWhenItRains;

/** Damages entity when it is in water. */
@Mod.EventBusSubscriber
public class CorrosionCurse extends WonderfulCurse {
	private static final String CORROSION_TAG = "CurseOfCorrosionCounter";
	protected final DurationConfig damageCooldown;
	protected final DoubleConfig damageAmount;

	public CorrosionCurse() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "Corrosion" );
		String cooldownComment = "Damage cooldown in seconds.";
		String damageComment = "Amount of damage dealt to the player every X seconds.";
		this.damageCooldown = new DurationConfig( "damage_cooldown_duration", cooldownComment, false, 3.0, 1.0, 60.0 );
		this.damageAmount = new DoubleConfig( "damage_amount", damageComment, false, 1.0, 1.0, 20.0 );
		this.curseGroup.addConfigs( this.damageCooldown, this.damageAmount );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		if( !( entity.world instanceof ServerWorld ) )
			return;

		CorrosionCurse corrosionCurse = Instances.CORROSION;
		ServerWorld world = ( ServerWorld )entity.world;
		int enchantmentLevel = EnchantmentHelperPlus.calculateEnchantmentSum( corrosionCurse, entity.getArmorInventoryList() );
		CompoundNBT data = entity.getPersistentData();

		int counter = data.getInt( CORROSION_TAG ) + 1;
		if( enchantmentLevel > 0 && isEntityOutsideWhenItRains( entity, world ) && counter > corrosionCurse.damageCooldown.getDuration() ) {
			counter -= corrosionCurse.damageCooldown.getDuration();
			entity.attackEntityFrom( DamageSource.DROWN, ( float )( enchantmentLevel * corrosionCurse.damageAmount.get() ) );
			damageArmor( entity );
		}
		data.putInt( CORROSION_TAG, counter );
	}

	/** Deals damage to each armor piece with corrosion curse. */
	protected static void damageArmor( LivingEntity entity ) {
		for( ItemStack itemStack : entity.getArmorInventoryList() )
			if( EnchantmentHelper.getEnchantmentLevel( Instances.CORROSION, itemStack ) > 0 ) {
				EquipmentSlotType equipmentSlotType = itemStack.getEquipmentSlot();

				if( equipmentSlotType != null )
					itemStack.damageItem( 1, entity, owner->owner.sendBreakAnimation( equipmentSlotType ) );
			}
	}
}
