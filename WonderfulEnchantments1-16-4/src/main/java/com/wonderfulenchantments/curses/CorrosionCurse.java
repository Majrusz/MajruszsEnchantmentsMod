package com.wonderfulenchantments.curses;

import com.mlib.EquipmentSlotTypes;
import com.mlib.config.DoubleConfig;
import com.mlib.config.DurationConfig;
import com.mlib.enchantments.EnchantmentHelperPlus;
import com.wonderfulenchantments.Instances;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Damages entity when it is in water. */
@Mod.EventBusSubscriber
public class CorrosionCurse extends WonderfulCurse {
	private static final String CORROSION_TAG = "CurseOfCorrosionCounter";
	protected final DurationConfig damageCooldown;
	protected final DoubleConfig damageAmount;

	public CorrosionCurse() {
		super( Rarity.RARE, EnchantmentType.ARMOR, EquipmentSlotTypes.ARMOR, "Corrosion" );
		String cooldownComment = "Damage cooldown in seconds.";
		this.damageCooldown = new DurationConfig( "damage_cooldown_duration", cooldownComment, false, 3.0, 1.0, 60.0 );
		String damageComment = "Amount of damage dealt to the player every X seconds.";
		this.damageAmount = new DoubleConfig( "damage_amount", damageComment, false, 1.0, 1.0, 20.0 );
		this.curseGroup.addConfig( this.damageCooldown );
		this.curseGroup.addConfig( this.damageAmount );

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
		}
		data.putInt( CORROSION_TAG, counter );
	}

	/** Checks whether entity is outside when it is raining. */
	protected static boolean isEntityOutsideWhenItRains( LivingEntity entity, ServerWorld world ) {
		BlockPos entityPosition = new BlockPos( entity.getPositionVec() );
		Biome biome = world.getBiome( entityPosition );

		return world.canSeeSky( entityPosition ) && world.isRaining() && biome.getPrecipitation() == Biome.RainType.RAIN;
	}
}
