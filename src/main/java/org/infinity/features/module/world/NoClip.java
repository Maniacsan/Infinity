package org.infinity.features.module.world;

import org.infinity.event.ClipEvent;
import org.infinity.event.MotionEvent;
import org.infinity.event.PlayerInWaterEvent;
import org.infinity.event.PlayerMoveEvent;
import org.infinity.event.PushOutBlockEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.utils.Helper;
import org.infinity.utils.MoveUtil;

import com.darkmagician6.eventapi.EventTarget;

@ModuleInfo(category = Category.WORLD, desc = "Allows you to walk through blocks", key = -2, name = "NoClip", visible = true)
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

	@Override
	public void onPlayerTick() {
	}

	@EventTarget
	public void onPlayerInWater(PlayerInWaterEvent event) {
	}

	@EventTarget
	public void onMove(PlayerMoveEvent event) {

	}

	@EventTarget
	public void onPushBlock(PushOutBlockEvent event) {
		event.cancel();
	}

	@EventTarget
	public void onClip(ClipEvent event) {
		event.cancel();
	}

}
