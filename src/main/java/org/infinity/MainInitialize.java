package org.infinity;

import org.infinity.main.InfMain;

import net.fabricmc.api.ModInitializer;

public class MainInitialize implements ModInitializer {

	@Override
	public void onInitialize() {
		InfMain.INSTANCE.initialize();
	}

}
