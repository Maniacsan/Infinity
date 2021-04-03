package me.infinity.features.module.movement;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.PlayerInWaterEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Lets you not swim underwater", key = -2, name = "NoSwim", visible = true)
public class NoSwim extends Module {

	@EventTarget
	public void onPlayerInWater(PlayerInWaterEvent event) {
		event.setInWater(false);
	}

}
