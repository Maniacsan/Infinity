package org.infinity.features.module.world;

import org.infinity.InfMain;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;

@ModuleInfo(category = Module.Category.WORLD, desc = "Editing game speed", key = -2, name = "Timer", visible = true)
public class Timer extends Module {

	private Settings value = new Settings(this, "Value", 1.1f, 0.3f, 10.0f, () -> true);
	
	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}
	
	@Override
	public void onPlayerTick() {
		InfMain.TIMER = value.getCurrentValueFloat();
	}

}
