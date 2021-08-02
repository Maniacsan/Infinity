package org.infinity.main;

import net.fabricmc.api.ModInitializer;

public class MainInitialize implements ModInitializer {

	@Override
	public void onInitialize() {
		InfMain.INSTANCE.initialize();
	}

}
