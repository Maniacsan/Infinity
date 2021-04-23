package org.infinity.features.module.player;

import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.StringUtil;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Allows you to dig blocks faster", key = -2, name = "FastBreak", visible = true)
public class FastBreak extends Module {

	public Settings speed = new Settings(this, "Speed", 1, 1.6, 3, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(StringUtil.DF(speed.getCurrentValueDouble(), 2));
	}

}
