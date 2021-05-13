package org.infinity.features.module.movement;

import org.infinity.event.PlayerInWaterEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;

import com.darkmagician6.eventapi.EventTarget;

@ModuleInfo(category = Category.MOVEMENT, desc = "Lets you not swim underwater", key = -2, name = "NoSwim", visible = true)
public class NoSwim extends Module {

	@EventTarget
	public void onPlayerInWater(PlayerInWaterEvent event) {
		// TODO In progress
		event.setInWater(false);
	}

}
