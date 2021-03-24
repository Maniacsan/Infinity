package me.infinity.utils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.MathHelper;
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
	
	public static void strafe(double speed) {
		if (!isMoving())
			return;
		float yaw = getYaw();
		double x = -Math.sin(yaw) * speed;
		double y = Math.cos(yaw) * speed;
		Helper.getPlayer().setVelocity(x, 0, y);
	}

	/**
	 * Silent strafe (matrix "BadStrafe" killaura check bypass)
	 * 
	 * @param yaw
	 */
	public static void silentStrafe(float yaw) {
		int diff = (int) ((MathHelper.wrapDegrees(Helper.getPlayer().yaw - yaw - 23.5f - 135) + 180) / 45);
		double forward = 0f;
		double strafe = 0f;
		double pForward = Helper.getPlayer().forwardSpeed;
		double pStrafe = Helper.getPlayer().sidewaysSpeed;

		switch (diff) {
		case 0:
			forward = pForward;
			strafe = pStrafe;
			break;
		case 1:
			forward += forward;
			strafe -= forward;
			forward += strafe;
			strafe += strafe;
			break;
		case 2:
			forward = strafe;
			strafe = -forward;
			break;
		case 3:
			forward -= forward;
			strafe -= forward;
			forward += strafe;
			strafe -= strafe;
			break;
		case 4:
			forward = -forward;
			strafe = -strafe;
			break;
		case 5:
			forward -= forward;
			strafe += forward;
			forward -= strafe;
			strafe -= strafe;
			break;
		case 6:
			forward = -strafe;
			strafe = forward;
			break;
		case 7:
			forward += forward;
			strafe += forward;
			forward -= strafe;
			strafe += strafe;
			break;
		}

		if (forward > 1f || forward < 0.9f && forward > 0.3f || forward < -1f || forward > -0.9f && forward < -0.3f) {
			forward *= 0.5;
		}
		if (strafe > 1f || strafe < 0.9f && strafe > 0.3f || strafe < -1f || strafe > -0.9f && strafe < -0.3f) {
			strafe *= 0.5f;
		}
		double d = strafe * strafe + forward * forward;
		updateVelocity(getBaseSpeed(), new Vec3d(strafe, 0, forward), d, yaw);
	}

	public static void updateVelocity(float speed, Vec3d movementInput, double d, float yaw) {
		Vec3d vec3d = movementInputToVelocity(movementInput, d, speed, yaw);
		Helper.getPlayer().setVelocity(Helper.getPlayer().getVelocity().add(vec3d));
	}

	public static Vec3d movementInputToVelocity(Vec3d movementInput, double d, float speed, float yaw) {
		if (d < 1.0E-4F) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d = (d > 1.0D ? movementInput.normalize() : movementInput).multiply((double) speed);
			float f = MathHelper.sin((float) (yaw * Math.PI / 180F));
			float g = MathHelper.cos((float) (yaw * Math.PI / 180f));
			return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f, vec3d.y,
					vec3d.z * (double) g + vec3d.x * (double) f);
		}
	}

	public static float getBaseSpeed() {
		float g = 0.005F;
		float h = EnchantmentHelper.getDepthStrider(Helper.getPlayer());
		if (h > 3.0F)
			h = 3.0F;
		if (!Helper.getPlayer().isOnGround())
			h *= 0.5F;
		if (h > 0.0F) {
			g += (Helper.getPlayer().getMovementSpeed() - g) * h / 3.0F;
		}
		return g;
	}

}