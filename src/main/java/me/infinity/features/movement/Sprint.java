package me.infinity.features.movement;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Auto sprinting", key = -2, name = "Sprint", visible = true)
public class Sprint extends Module {

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().forwardSpeed != 0) {
			Helper.minecraftClient.options.keySprint.setPressed(true);
		}
	}

}
