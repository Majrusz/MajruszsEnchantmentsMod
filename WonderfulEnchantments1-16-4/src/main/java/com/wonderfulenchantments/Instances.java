package com.wonderfulenchantments;

import com.wonderfulenchantments.curses.FatigueCurse;
import com.wonderfulenchantments.curses.IncompatibilityCurse;
import com.wonderfulenchantments.curses.SlownessCurse;
import com.wonderfulenchantments.curses.VampirismCurse;
import net.minecraftforge.fml.ModLoadingContext;

public class Instances {
	// Curses
	public static final SlownessCurse SLOWNESS;
	public static final FatigueCurse FATIGUE;
	public static final IncompatibilityCurse INCOMPATIBILITY;
	public static final VampirismCurse VAMPIRISM;

	static {
		SLOWNESS = new SlownessCurse();
		FATIGUE = new FatigueCurse();
		INCOMPATIBILITY = new IncompatibilityCurse();
		VAMPIRISM = new VampirismCurse();

		WonderfulEnchantments.CONFIG_HANDLER.register( ModLoadingContext.get() );
	}
}
