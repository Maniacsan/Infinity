package org.infinity.features.module.movement;

import org.infinity.event.PlayerInWaterEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;

import com.darkmagician6.eventapi.EventTarget;

@ModuleInfo(category = Category.MOVEMENT, desc = "You can walk underwater", key = -2, name = "AntiWaterPush", visible = true)
public class AntiWaterPush extends Module {

	@EventTarget
	public void onPlayerInWater(PlayerInWaterEvent event) {
		event.setInWater(false);
	}
	
	@Override
	public void onUpdate() {
		Helper.getPlayer().setSprinting(false);
	}

}
