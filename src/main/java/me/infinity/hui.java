package me.infinity;

import net.fabricmc.api.ModInitializer;

public class MainInitialize implements ModInitializer {

	@Override
	public void onInitialize() {
		InfMain.INSTANCE.initialize();
	}

}
