package me.infinity.features.module.player;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Allows you to dig blocks faster", key = -2, name = "FastBreak", visible = true)
public class FastBreak extends Module {

	public Settings speed = new Settings(this, "Speed", 1, 1.6, 3, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(Helper.DF(speed.getCurrentValueDouble(), 2));
	}

}
