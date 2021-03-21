package me.infinity.features.module.combat;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.ClickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.entity.EntityUtil;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Hit a player from a long distance", key = -2, name = "Reach", visible = true)
public class Reach extends Module {

	public Settings reach = new Settings(this, "Range", 4.1D, 0D, 6.0D, () -> true);

	@Override
	public void onPlayerTick() {
		setSuffix(String.valueOf(reach.getCurrentValueDouble()));
	}

	@EventTarget
	public void onClick(ClickEvent event) {
		
		EntityUtil.updateTargetRaycast(Helper.minecraftClient.targetedEntity, reach.getCurrentValueDouble(),
				Helper.getPlayer().yaw, Helper.getPlayer().pitch);

	}
}
