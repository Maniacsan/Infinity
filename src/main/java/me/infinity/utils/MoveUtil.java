package me.infinity.utils;

public class MoveUtil {

	/**
	 * Checking player to move
	 * 
	 * @return
	 */
	public boolean isMoving() {
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

}