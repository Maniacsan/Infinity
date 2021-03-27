package me.infinity.features.module.visual;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Removes shadows, adds brightness", key = -2, name = "FullBright", visible = true)
public class FullBright extends Module {

	private double oldGamma = -2;

	@Override
	public void onEnable() {
		oldGamma = Helper.minecraftClient.options.gamma;
	}

	@Override
	public void onDisable() {
		if (oldGamma != -2) {
			Helper.minecraftClient.options.gamma = oldGamma;
			oldGamma = -2;
		}
	}

	@Override
	public void onPlayerTick() {
		Helper.minecraftClient.options.gamma = 100;
	}

}
