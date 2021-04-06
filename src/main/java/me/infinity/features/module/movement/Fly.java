package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.InfMain;
import me.infinity.event.MotionEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Lets fly in survival", key = -2, name = "Fly", visible = true)
public class Fly extends Module {

	private Settings mode = new Settings(this, "Mode", "Motion",
			new ArrayList<>(Arrays.asList("Motion", "Glide")), () -> true);

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
					MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() + 0.23);
				}
				if (Helper.minecraftClient.options.keySneak.isPressed()) {
					MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() - 0.23);
				}

				MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed() * 1.4);

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
