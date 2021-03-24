package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.event.PlayerMoveEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Make you faster", key = -2, name = "Speed", visible = true)
public class Speed extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix", new ArrayList<>(Arrays.asList("Matrix")), () -> true);

	private double dir;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMove(PlayerMoveEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix")) {
			if (Helper.getPlayer().isOnGround()) {
				if (Helper.getPlayer().age % 3 == 0) {
					if (Helper.minecraftClient.options.keyForward.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 2.5), 0,
								Math.cos(dir) * 2.5);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * 1.3), 0,
								Math.cos(dir) * 1.3);
					}
					if (Helper.minecraftClient.options.keyLeft.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw - 90);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.24), 0,
								Math.cos(dir) * 0.24);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * -0.1), 0,
								Math.cos(dir) * -0.1);
					}
					if (Helper.minecraftClient.options.keyRight.isPressed()) {
						dir = Math.toRadians(Helper.getPlayer().yaw + 90);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * 0.24), 0,
								Math.cos(dir) * 0.24);

						Helper.getPlayer().setVelocity((-Math.sin(dir) * -0.1), 0,
								Math.cos(dir) * -0.1);
					}
				}
				if (!Helper.getPlayer().isOnGround()) {
					int reduce = 1;
					if (reduce == 1) {
						dir = Math.toRadians(Helper.getPlayer().yaw);
						Helper.getPlayer().setVelocity((-Math.sin(dir) * -0.11), 0,
								Math.cos(dir) * -0.11);
						reduce = 0;
					}
				}
			}
		}

	}

}
