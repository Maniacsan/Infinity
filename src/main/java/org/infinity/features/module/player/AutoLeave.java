package org.infinity.features.module.player;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.InfMain;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Module.Category.PLAYER, desc = "If at a certain radius the player appears automatically exits | writes / spawns", key = -2, name = "AutoLeave", visible = true)
public class AutoLeave extends Module {

	private Settings mode = new Settings(this, "Mode", "/spawn", new ArrayList<>(Arrays.asList("/spawn", "Disconnect")),
			() -> true);

	private Settings ignoreFriends = new Settings(this, "Ignore Friends", true, () -> true);

	private Settings radius = new Settings(this, "Radius", 20D, 1D, 50D, () -> true);

	@Override
	public void onPlayerTick() {
		for (Entity e : Helper.getWorld().getPlayers()) {
			if (e == Helper.getPlayer() || e == Helper.getPlayer().getVehicle()
					|| ignoreFriends.isToggle() && InfMain.getFriend().check(e.getEntityName()))
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
