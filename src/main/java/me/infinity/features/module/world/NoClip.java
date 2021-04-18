package me.infinity.features.module.world;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.event.PushOutBlockEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;

@ModuleInfo(category = Module.Category.WORLD, desc = "Allows you to walk through blocks", key = -2, name = "NoClip", visible = true)
public class NoClip extends Module {

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		MoveUtil.setYVelocity(0);

		if (Helper.minecraftClient.options.keySneak.isPressed()) {
			MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().y - 0.2);
		} else if (Helper.minecraftClient.options.keyJump.isPressed()) {
			MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().y + 0.2);
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
