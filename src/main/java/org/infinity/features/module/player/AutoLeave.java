package org.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Category.PLAYER, desc = "If at a certain radius the player appears automatically exits | writes / spawns", key = -2, name = "AutoLeave", visible = true)
public class AutoLeave extends Module {

	private Setting mode = new Setting(this, "Mode", "/spawn",
			new ArrayList<>(Arrays.asList("/spawn", "Disconnect")));

	private Setting ignoreFriends = new Setting(this, "Ignore Friends", true);

	private Setting radius = new Setting(this, "Radius", 20D, 1D, 50D);

	@Override
	public void onUpdate() {
		for (Entity e : Helper.getWorld().getPlayers()) {
			if (e == Helper.getPlayer() || e == Helper.getPlayer().getVehicle()
					|| ignoreFriends.isToggle() && InfMain.getFriend().contains(e.getEntityName()))
				continue;

			if (Helper.getPlayer().distanceTo(e) <= radius.getCurrentValueDouble()) {
				if (mode.getCurrentMode().equalsIgnoreCase("/spawn")) {
					Helper.getPlayer().sendChatMessage("/spawn");
					setEnabled(false);
				} else if (mode.getCurrentMode().equalsIgnoreCase("Disconnect")) {
					Helper.getWorld().disconnect();
					setEnabled(false);
				}
			}
		}
	}

}
