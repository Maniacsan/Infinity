package me.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PlayerMoveEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Make you faster", key = -2, name = "Speed", visible = true)
public class Speed extends Module {

	private Settings mode = new Settings(this, "Mode", "Matrix",
			new ArrayList<>(Arrays.asList("Matrix 6.0.6", "OnGround")), () -> true);

	private double dir;
	private int yTick;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
				if (yTick >= 4 && Helper.getPlayer().isOnGround())
					yTick = 0;

				if (Helper.getPlayer().isOnGround()) {
					MoveUtil.setYVelocity(yTick <= 1 ? 0.42F : 0.4F);

					float f = Helper.getPlayer().yaw * 0.017453292F;
					MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().getX() - Math.sin(f) * 0.2F,
							Helper.getPlayer().getVelocity().getZ() + Math.cos(f) * 0.2F);

					yTick++;
				} else if (yTick <= 1)
					MoveUtil.setYVelocity(-5D);
			}
		}
	}

	@EventTarget
	public void onMove(PlayerMoveEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("OnGround")) {
			if (Helper.getPlayer().isOnGround()) {
				MoveUtil.getHorizontalVelocity(5.35, Helper.getPlayer().yaw);

			}
			if (!Helper.getPlayer().isOnGround()) {
				int reduce = 1;
				if (reduce == 1) {
					dir = Math.toRadians(Helper.getPlayer().yaw);
					Helper.getPlayer().setVelocity((-Math.sin(dir) * -0.11), Helper.getPlayer().getVelocity().y,
							Math.cos(dir) * -0.11);
					reduce = 0;
				}
			}

		} else if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
			if (MoveUtil.isMoving()) {
				if (Helper.getPlayer().age % 2 == 0)
					MoveUtil.strafe(0.3);

			}
		}

	}

}
