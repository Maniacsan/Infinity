package me.infinity.features.module.player;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.MathAssist;

@ModuleInfo(category = Module.Category.PLAYER, desc = "Automatically takes things from the chest", key = -2, name = "ChestSteal", visible = true)
public class ChestSteal extends Module {

	public Settings maxDelay = new Settings(this, "Max Delay", 40.0D, 0D, 190D, () -> true);
	public Settings minDelay = new Settings(this, "Min Delay", 10.0D, 0D, 190D, () -> true);

	public double getDelay() {
		return MathAssist.random(minDelay.getCurrentValueDouble(), maxDelay.getCurrentValueDouble());
	}

}
