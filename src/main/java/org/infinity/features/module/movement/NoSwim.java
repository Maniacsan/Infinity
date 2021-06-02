package org.infinity.features.module.movement;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

@ModuleInfo(category = Category.MOVEMENT, desc = "Lets you not swim underwater", key = -2, name = "NoSwim", visible = true)
public class NoSwim extends Module {

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().isTouchingWater()) {
			Helper.getPlayer().setSwimming(false);
		}
	}

}
