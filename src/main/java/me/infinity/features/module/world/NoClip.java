package me.infinity.features.module.world;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.event.PushOutBlockEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;

@ModuleInfo(category = Module.Category.WORLD, desc = "Allows you to walk through blocks", key = -2, name = "NoClip", visible = true)
public class NoClip extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix", new ArrayList<>(Arrays.asList("Vanilla", "Matrix")),
			() -> true);

	private int timer;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {
				MoveUtil.setYVelocity(0);
			}

		}
	}
	
	@EventTarget
	public void onMove(PlayerMoveEvent event) {
		Helper.getPlayer().noClip = true;
	}

	@EventTarget
	public void onPushBlock(PushOutBlockEvent event) {
		event.cancel();
	}


}
