package com.majruszsenchantments;

import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {
	@Override
	public void onInitialize() {
		MajruszsEnchantments.HELPER.register();
	}
}
