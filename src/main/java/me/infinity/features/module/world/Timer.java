package me.infinity.features.module.world;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;

@ModuleInfo(category = Module.Category.WORLD, desc = "Editing game speed", key = -2, name = "Timer", visible = true)
public class Timer extends Module {

	private Settings value = new Settings(this, "Value", 1.1f, 0.3f, 10.0f, () -> true);

	public float getSpeed() {
		return isEnabled() ? value.getCurrentValueFloat() : 1f;
	}

}
