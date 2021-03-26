package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IMinecraftClient;
import me.infinity.utils.Helper;
import me.infinity.utils.TimeHelper;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Clicking on pressed LKM", key = -2, name = "AutoClicker", visible = true)
public class AutoClicker extends Module {

	private Settings coolDown = new Settings(this, "Cool Down", true, () -> true);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.0D, 10.0D, () -> !coolDown.isToggle());

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onPlayerTick() {
		if (Helper.minecraftClient.options.keyAttack.isPressed()) {

			if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
					: timer.hasReached(1000 / aps.getCurrentValueDouble())) {

				((IMinecraftClient) Helper.minecraftClient).mouseClick();

				Helper.getPlayer().resetLastAttackedTicks();
				timer.reset();
			}
		}
	}

}
