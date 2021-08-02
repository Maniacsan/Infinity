package org.infinity.features.module.visual;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

@ModuleInfo(category = Category.VISUAL, desc = "Removes shadows, adds brightness", key = -2, name = "FullBright", visible = true)
public class FullBright extends Module {

	private double oldGamma = -2;

	@Override
	public void onEnable() {
		oldGamma = Helper.MC.options.gamma;
	}

	@Override
	public void onDisable() {
		if (oldGamma != -2) {
			Helper.MC.options.gamma = oldGamma;
			oldGamma = -2;
		}
	}

	@Override
	public void onUpdate() {
		Helper.MC.options.gamma = 100;
	}

}
