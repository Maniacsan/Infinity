package org.infinity.utils.rotation;

import org.infinity.utils.Helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {

	/**
	 * This rotation with predict for aim from long distance
	 * 
	 * @param target
	 * @param maxYawChange
	 * @param maxPitchChange
	 * @return
	 */
	public static float[] bowAimRotation(Entity target) {
		double xPos = target.getX();
		double zPos = target.getZ();
		double yPos;
		if (target instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) target;
			yPos = livingEntity.getEyeY() - Helper.getPlayer().getEyeY() - 0.5 + Helper.getPlayer().getY();
		} else {
			yPos = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0D - Helper.getPlayer().getY()
					+ Helper.getPlayer().getEyeY() - 0.5;
		}

		double sideMultiplier = Helper.getPlayer().distanceTo(target)
				/ ((Helper.getPlayer().distanceTo(target) / 2) / 1) * 5;
		double upMultiplier = (Helper.getPlayer().squaredDistanceTo(target) / 320) * 1.1;
		Vec3d vecPos = new Vec3d((xPos - 0.5) + (xPos - target.lastRenderX) * sideMultiplier, yPos + upMultiplier,
				(zPos - 0.5) + (zPos - target.lastRenderZ) * sideMultiplier);
		float[] lookVec = lookAtVecPos(vecPos);
		return new float[] { lookVec[0], lookVec[1] };
	}

	/**
	 * Look to target -> float[] look = RotationUtils.lookAtEntity(values):
	 * 
	 * @param targetEntity
	 * @param maxYawChange
	 * @param maxPitchChange
	 */
	public static float[] lookAtEntity(Entity targetEntity) {
		double d = targetEntity.getX() - Helper.getPlayer().getX();
		double e = targetEntity.getZ() - Helper.getPlayer().getZ();
		double g;
		if (targetEntity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) targetEntity;
			g = livingEntity.getEyeY() - Helper.getPlayer().getEyeY() - 0.5;
		} else {
			g = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0D
					- Helper.getPlayer().getY() + Helper.getPlayer().getEyeY() - 0.5;
		}

		double h = (double) Math.sqrt(d * d + e * e);
		float i = (float) (Math.atan2(e, d) * 180.0D / Math.PI) - 90.0F;
		float j = (float) (-(Math.atan2(g, h) * 180.0D / Math.PI));
		float yaw = i;
		float pitch = j;
		return new float[] { yaw, pitch };
	}

	/**
	 * Look to target with Vec3d component
	 * 
	 * @param targetEntity
	 * @param maxYawChange
	 * @param maxPitchChange
	 * @return
	 */
	public static float[] lookAtVecPos(Vec3d targetEntity) {
		double d = targetEntity.getX() + 0.5 - Helper.getPlayer().getX();
		double g = targetEntity.getY() - Helper.getPlayer().getY();
		double e = targetEntity.getZ() + 0.5 - Helper.getPlayer().getZ();

		double h = (double) Math.sqrt(d * d + e * e);
		float i = (float) (Math.atan2(e, d) * 180.0D / Math.PI) - 90.0F;
		float j = (float) (-(Math.atan2(g, h) * 180.0D / Math.PI));
		float yaw = i;
		float pitch = j;
		return new float[] { yaw, pitch };
	}

	public static float[] getLookNeeded(double x, double y, double z) {
		double d = x + 0.5 - Helper.getPlayer().getX();
		double g = y - Helper.getPlayer().getY();
		double e = z + 0.5 - Helper.getPlayer().getZ();

		double h = (double) Math.sqrt(d * d + e * e);
		float i = (float) (Math.atan2(e, d) * 180.0D / Math.PI) - 90.0F;
		float j = (float) (-(Math.atan2(g, h) * 180.0D / Math.PI));
		return new float[] { i, j };
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static float limitAngleChange(final float currentRotation, final float targetRotation,
			final float turnSpeed) {
		final float diff = RotationUtils.getAngleDifference(targetRotation, currentRotation);

		return currentRotation + (diff > turnSpeed ? turnSpeed : Math.max(diff, -turnSpeed));
	}

	public static float updateAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
		float f = MathHelper.wrapDegrees(newAngle - oldAngle);
		if (f > maxChangeInAngle) {
			f = maxChangeInAngle;
		}

		if (f < -maxChangeInAngle) {
			f = -maxChangeInAngle;
		}

		return oldAngle + f;
	}

	public static boolean isInFOV(Entity entity, double angle) {
		double angleDiff = getAngle360(Helper.getPlayer().yaw,
				getLookNeeded(entity.getX(), entity.getY(), entity.getZ())[0]);
		return angleDiff > 0.0 && angleDiff < (angle *= 0.5) || -angle < angleDiff && angleDiff < 0.0;
	}

	public static boolean isInFOVPos(BlockPos pos, double angle) {
		double angleDiff = getAngle360(Helper.getPlayer().yaw, getLookNeeded(pos.getX(), pos.getY(), pos.getZ())[0]);
		return angleDiff > 0.0 && angleDiff < (angle *= 0.5) || -angle < angleDiff && angleDiff < 0.0;
	}

	private static float getAngle360(float dir, float yaw) {
		float f = Math.abs(yaw - dir) % 360.0f;
		float dist = f > 180.0f ? 360.0f - f : f;
		return dist;
	}

	public static float getYaw(Entity entity) {
		double x = entity.getX() - Helper.getPlayer().getX();
		double z = entity.getZ() - Helper.getPlayer().getZ();
		float yaw = (float) (Math.atan2(x, z) * 57.29577951308232);
		return yaw;
	}

	// Entity.class raycasting vector rotations

	public static final Vec3d getRotationVec(float yaw, float pitch) {
		return getRotationVector(pitch, yaw);
	}

	protected static final Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * 0.017453292F;
		float g = -yaw * 0.017453292F;
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d((double) (i * j), (double) (-k), (double) (h * j));
	}

	public static Vec3d getEyesPos() {
		return new Vec3d(Helper.getPlayer().getX(),
				Helper.getPlayer().getY() + Helper.getPlayer().getEyeHeight(Helper.getPlayer().getPose()),
				Helper.getPlayer().getZ());
	}

}