package org.infinity.features.module.combat;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.mixin.IMinecraftClient;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;

@ModuleInfo(category = Category.COMBAT, desc = "Clicking on pressed LKM", key = -2, name = "AutoClicker", visible = true)
public class AutoClicker extends Module {

	private Setting coolDown = new Setting(this, "Cool Down", true);
	private Setting aps = new Setting(this, "APS", 1.8D, 0.0D, 10.0D).setVisible(() -> !coolDown.isToggle());

	private Timer timer = new Timer();

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
