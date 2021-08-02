package org.infinity.features.module.world;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;

@ModuleInfo(category = Category.WORLD, desc = "Editing game speed", key = -2, name = "Timer", visible = true)
public class Timer extends Module {

	private Setting value = new Setting(this, "Value", 1.1f, 0.3f, 10.0f);
	
	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}
	
	@Override
	public void onUpdate() {
		InfMain.TIMER = value.getCurrentValueFloat();
	}

}
