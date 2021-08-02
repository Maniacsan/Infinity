package org.infinity.features.module.movement;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

@ModuleInfo(category = Category.MOVEMENT, desc = "Auto sprinting", key = -2, name = "Sprint", visible = true)
public class Sprint extends Module {

	@Override
	public void onUpdate() {
		if (Helper.getPlayer().forwardSpeed != 0) {
			Helper.MC.options.keySprint.setPressed(true);
		}
	}

}
