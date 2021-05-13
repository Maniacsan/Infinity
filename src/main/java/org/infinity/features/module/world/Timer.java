package org.infinity.features.module.world;

import org.infinity.InfMain;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;

@ModuleInfo(category = Category.WORLD, desc = "Editing game speed", key = -2, name = "Timer", visible = true)
public class Timer extends Module {

	private Setting value = new Setting(this, "Value", 1.1f, 0.3f, 10.0f);
	
	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}
	
	@Override
	public void onPlayerTick() {
		InfMain.TIMER = value.getCurrentValueFloat();
	}

}
