package org.infinity.features.module.player;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.StringUtil;

@ModuleInfo(category = Category.PLAYER, desc = "Allows you to dig blocks faster", key = -2, name = "FastBreak", visible = true)
public class FastBreak extends Module {

	public Setting speed = new Setting(this, "Speed", 1, 1.6, 3);

	@Override
	public void onUpdate() {
		setSuffix(StringUtil.DF(speed.getCurrentValueDouble(), 2));
	}

}
