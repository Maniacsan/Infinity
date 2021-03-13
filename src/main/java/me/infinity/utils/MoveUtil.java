package me.infinity.utils;

import net.minecraft.util.math.Vec3d;

public class MoveUtil {

	/**
	 * Checking player to move
	 * 
	 * @return
	 */
	public static boolean isMoving() {
		return (Helper.getPlayer().forwardSpeed != 0 || Helper.getPlayer().sidewaysSpeed != 0);
	}

	public static float getYaw() {
		float moveYaw = (Helper.getPlayer()).yaw;
		float forward = 1.0F;
		if ((Helper.getPlayer()).forwardSpeed < 0.0F) {
			moveYaw += 180.0F;
			forward = -0.5F;
		}
		if ((Helper.getPlayer()).forwardSpeed > 0.0F)
			forward = 0.5F;
		if ((Helper.getPlayer()).sidewaysSpeed > 0.0F)
			moveYaw -= 90.0F * forward;
		if ((Helper.getPlayer()).sidewaysSpeed < 0.0F)
			moveYaw += 90.0F * forward;
		moveYaw = (float) Math.toRadians(moveYaw);
		return moveYaw;
	}

	public static void silentStrafe(float speed) {
		if (isMoving()) {
			Helper.getPlayer().setVelocity(new Vec3d(0, Helper.getPlayer().getVelocity().y, 0));
			Helper.getPlayer().updateVelocity(speed,
					new Vec3d(Helper.getPlayer().sidewaysSpeed, 0, Helper.getPlayer().forwardSpeed));
		}
	}

}