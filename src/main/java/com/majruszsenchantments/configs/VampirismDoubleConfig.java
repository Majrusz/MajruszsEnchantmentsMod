package com.majruszsenchantments.configs;

import com.majruszsenchantments.Registries;
import com.mlib.EquipmentSlots;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.mlib.math.Range;
import net.minecraft.world.entity.LivingEntity;

public class VampirismDoubleConfig extends ConfigGroup {
	final DoubleConfig baseChance;
	final DoubleConfig bonusChance;

	public VampirismDoubleConfig( double baseChance, double vampirismBonusChance ) {
		this.baseChance = new DoubleConfig( baseChance, Range.CHANCE );
		this.bonusChance = new DoubleConfig( vampirismBonusChance, Range.CHANCE );

		this.addConfig( this.baseChance.name( "base_chance" ).comment( "Base chance of this to happen." ) );
		this.addConfig( this.bonusChance.name( "bonus_chance" ).comment( "Bonus chance of this to happen per Curse of Vampirism level." ) );
	}

	public double getTotalChance( LivingEntity entity ) {
		return this.baseChance.get() + Registries.VAMPIRISM.get().getEnchantmentSum( entity, EquipmentSlots.ARMOR ) * this.bonusChance.get();
	}
}
