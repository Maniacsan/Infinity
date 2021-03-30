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
			new ArrayList<>(Arrays.asList("Matrix 6.0.6", "OnGround", "Strafe")), () -> true);

	private double dir;
	private int yTick;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		if (mode.getCurrentMode().equalsIgnoreCase("Strafe")) {

			Helper.getPlayer().flyingSpeed = 0.029f;
			
			if (MoveUtil.isMoving()) {
				if (Helper.getPlayer().isOnGround())
					Helper.getPlayer().jump();

				MoveUtil.strafe(MoveUtil.calcMoveYaw(), MoveUtil.getSpeed());

				if (Helper.getPlayer().isSprinting()) {
					MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.2);
				}

			}
		}
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			if (mode.getCurrentMode().equalsIgnoreCase("Matrix 6.0.6")) {
				if (MoveUtil.isMoving()) {

					float yaw = Helper.getPlayer().yaw;
					float f = (float) (Helper.minecraftClient.options.mouseSensitivity * 0.6F + 0.2F);
					float gcd = f * f * f * 1.2F;

					yaw -= yaw % gcd;
					
					MoveUtil.strafe(0);

					if (Helper.getPlayer().age % 3 == 0) {
						if (Helper.minecraftClient.options.keyForward.isPressed()) {
							dir = Math.toRadians(yaw);
							MoveUtil.strafe(MoveUtil.calcMoveYaw(), 11);

							MoveUtil.strafe(MoveUtil.calcMoveYaw(), 0.57);
						}
						if (Helper.minecraftClient.options.keyLeft.isPressed()) {
							dir = Math.toRadians(Helper.getPlayer().yaw - 90);
							MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().x + -Math.sin(dir) * 0.24,
									Helper.getPlayer().getVelocity().z + Math.cos(dir) * 0.24);

							MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().x + -Math.sin(dir) * -0.1,
									Helper.getPlayer().getVelocity().z + Math.cos(dir) * -0.1);
						}
						if (Helper.minecraftClient.options.keyRight.isPressed()) {
							dir = Math.toRadians(Helper.getPlayer().yaw + 90);
							MoveUtil.setHVelocity(Helper.getPlayer().getVelocity().x + -Math.sin(dir) * 0.24,
									Helper.getPlayer().getVelocity().z + Math.cos(dir) * 0.24);

							Helper.getPlayer().updateTrackedPosition(
									Helper.getPlayer().getVelocity().x + -Math.sin(dir) * -0.1,
									Helper.getPlayer().getVelocity().getY(),
									Helper.getPlayer().getVelocity().z + Math.cos(dir) * -0.1);
						}
					}
				}
			}
		} else if (event.getType().equals(EventType.POST)) {
			double d = Math.abs(Helper.getPlayer().getVelocity().y);
			if (d < 0.1D && !Helper.getPlayer().bypassesSteppingEffects()) {

				double e = 0.4D + d * 0.2D;
				Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().multiply(e, 1.0D, e));
			}
		}
	}

	@EventTarget
	public void onMove(PlayerMoveEvent event) {
		if (mode.getCurrentMode().equalsIgnoreCase("OnGround")) {
			if (Helper.getPlayer().isOnGround()) {
				MoveUtil.getHorizontalVelocity(4.35, Helper.getPlayer().yaw);
			}

			if (Helper.getPlayer().isOnGround()) {
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
				MoveUtil.getHorizontalVelocity(13.2, Helper.getPlayer().yaw);
			}
		}

	}

}
