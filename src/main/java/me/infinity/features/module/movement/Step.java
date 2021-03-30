package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;

import me.infinity.InfMain;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.entity.EntityUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Let you walk up Blocks very fast", key = -2, name = "Step", visible = true)
public class Step extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix 6.0.6",
			new ArrayList<>(Arrays.asList("Intave", "Matrix 6.0.6")), () -> true);

	private boolean hasStep;

	@Override
	public void onDisable() {
		InfMain.resetTimer();
	}

	@EventTarget
	public void onTick(TickEvent event) {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Intave")) {

			EntityUtil.setStepHeight(0.52f);

			if (hasStep) {
				MoveUtil.getHorizontalVelocity(0.9, Helper.getPlayer().yaw);
				InfMain.TIMER = 0.9f + Helper.getPlayer().age % 4 / 16f;
				MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() - 0.01);
				MoveUtil.getHorizontalVelocity(0.002, Helper.getPlayer().yaw);

				if (Helper.getPlayer().fallDistance <= 0.05) {
					MoveUtil.getHorizontalVelocity(0.5, Helper.getPlayer().yaw);
					MoveUtil.strafe(0.05);
				}
			}

			if (Helper.getPlayer().isOnGround()) {
				InfMain.resetTimer();
				hasStep = false;
			}

			if (Helper.getPlayer().horizontalCollision && Helper.getPlayer().isOnGround()) {
				float y = (float) Math.max(1.1, 1.2);
				hasStep = true;
				InfMain.TIMER = y;
				Helper.getPlayer().jump();
				MoveUtil.getHorizontalVelocity(5.5, Helper.getPlayer().yaw);

			}

		}
	}

	@Override
	public void onPlayerTick() {
		if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
			if (hasStep) {
				InfMain.TIMER = 0.9f + Helper.getPlayer().age % 4 / 20f;
			}

			if (Helper.getPlayer().isOnGround()) {
				InfMain.resetTimer();
				hasStep = false;
			}

			if (Helper.getPlayer().horizontalCollision && Helper.getPlayer().isOnGround()) {
				hasStep = true;
				InfMain.TIMER = 3f;
				MoveUtil.setYVelocity(Helper.getPlayer().getVelocity().getY() + 0.47);

			}
		}
	}

}
