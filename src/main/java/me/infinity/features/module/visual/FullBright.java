package me.infinity.features.module.visual;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Removes shadows, adds brightness", key = -2, name = "FullBright", visible = true)
public class FullBright extends Module {

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer() != null && Helper.getWorld() != null) {
			Helper.minecraftClient.options.gamma = 1.0f;
		}
	}

}
