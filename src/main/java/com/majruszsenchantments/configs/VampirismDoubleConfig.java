package com.majruszsenchantments.configs;


import com.mlib.EquipmentSlots;
import com.mlib.config.ConfigGroup;
import com.mlib.config.DoubleConfig;
import com.majruszsenchantments.Registries;
import net.minecraft.world.entity.LivingEntity;

public class VampirismDoubleConfig extends ConfigGroup {
	final DoubleConfig baseChance;
	final DoubleConfig bonusChance;

	public VampirismDoubleConfig( String name, String comment, double baseChance, double vampirismBonusChance ) {
		super( name, comment );
		this.baseChance = new DoubleConfig( "base_chance", "Base chance of this to happen.", false, baseChance, 0.0, 1.0 );
		this.bonusChance = new DoubleConfig( "bonus_chance", "Bonus chance of this to happen per Curse of Vampirism level.", false, vampirismBonusChance, 0.0, 1.0 );
		this.addConfigs( this.baseChance, this.bonusChance );
	}

	public double getTotalChance( LivingEntity entity ) {
		return this.baseChance.get() + Registries.VAMPIRISM.get().getEnchantmentSum( entity, EquipmentSlots.ARMOR ) * this.bonusChance.get();
	}
}
