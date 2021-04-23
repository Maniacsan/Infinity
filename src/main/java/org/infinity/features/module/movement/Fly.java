package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.InfMain;
import org.infinity.event.MotionEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.MoveUtil;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Lets fly in survival", key = -2, name = "Fly", visible = true)
public class Fly extends Module {

	private Settings mode = new Settings(this, "Mode", "Motion",
			new ArrayList<>(Arrays.asList("Motion", "Glide")), () -> true);
	
	private Settings speed = new Settings(this, "Speed", 0.26, 0.15, 0.5, () -> true);

	private int airTicks;

	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {

			if (mode.getCurrentMode().equalsIgnoreCase("Motion")) {
				MoveUtil.setYVelocity(0);

				if (Helper.minecraftClient.options.keyJump.isPressed()) {
					MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() + speed.getCurrentValueDouble());
				}
				if (Helper.minecraftClient.options.keySneak.isPressed()) {
					MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() - speed.getCurrentValueDouble());
				}

				MoveUtil.strafe(MoveUtil.calcMoveYaw(), speed.getCurrentValueDouble());

			} else if (mode.getCurrentMode().equalsIgnoreCase("Glide")) {

				if (!Helper.getPlayer().isOnGround()) {
					airTicks++;
				}

				if (airTicks == 2) {
					InfMain.resetTimer();
				}

				if (airTicks == 6) {
					InfMain.TIMER = 0.7f;
					MoveUtil.setYVelocity(0.1);
				}

				if (airTicks >= 6 && !Helper.getPlayer().isOnGround()) {
					airTicks = 0;
					MoveUtil.vClip(-0.2);
				}
			}
		}
	}

}
