package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlots;
import com.mlib.LevelHelper;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.nbt.NBTHelper;
import com.wonderfulenchantments.Instances;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Damages entity when it is in water or outside while it is raining. */
@Mod.EventBusSubscriber
public class CorrosionCurse extends WonderfulCurse {
	private static final String CORROSION_TAG = "CurseOfCorrosionCounter";
	protected final DurationConfig damageCooldown;
	protected final DoubleConfig damageAmount;

	public CorrosionCurse() {
		super( "corrosion_curse", Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlots.ARMOR, "Corrosion" );

		String cooldownComment = "Damage cooldown in seconds.";
		this.damageCooldown = new DurationConfig( "damage_cooldown_duration", cooldownComment, false, 3.0, 1.0, 60.0 );

		String damageComment = "Amount of damage dealt to the player every X seconds. (with each enchantment level)";
		this.damageAmount = new DoubleConfig( "damage_amount", damageComment, false, 0.25, 0.0, 20.0 );

		this.curseGroup.addConfigs( this.damageCooldown, this.damageAmount );

		setMaximumEnchantmentLevel( 1 );
		setDifferenceBetweenMinimumAndMaximum( 40 );
		setMinimumEnchantabilityCalculator( level->10 );
	}

	@SubscribeEvent
	public static void onUpdate( LivingEvent.LivingUpdateEvent event ) {
		LivingEntity entity = event.getEntityLiving();
		if( !( entity.level instanceof ServerLevel ) )
			return;

		CorrosionCurse corrosionCurse = Instances.CORROSION;
		int enchantmentLevel = corrosionCurse.getEnchantmentSum( entity.getArmorSlots() );
		NBTHelper.IntegerData integerData = new NBTHelper.IntegerData( entity, CORROSION_TAG );

		integerData.set( value->value + 1 );
		boolean hasContactWithWater = LevelHelper.isEntityOutsideWhenItIsRaining( entity ) || entity.isInWater();
		if( enchantmentLevel > 0 && hasContactWithWater && integerData.get() > corrosionCurse.damageCooldown.getDuration() ) {
			integerData.set( 0 );
			if( corrosionCurse.damageAmount.get() > 0 )
				entity.hurt( DamageSource.DROWN, ( float )( enchantmentLevel * corrosionCurse.damageAmount.get() ) );
			corrosionCurse.damageArmor( entity );
		}
	}

	/** Deals damage to each armor piece with corrosion curse. */
	protected void damageArmor( LivingEntity entity ) {
		for( EquipmentSlot equipmentSlotType : EquipmentSlots.ARMOR ) {
			ItemStack itemStack = entity.getItemBySlot( equipmentSlotType );
			if( hasEnchantment( itemStack ) )
				itemStack.hurtAndBreak( 1, entity, owner->owner.broadcastBreakEvent( equipmentSlotType ) );
		}
	}
}
