package me.infinity.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {

	/**
	 * MoveControl.class paste for change angle fov to target Yaw/Pitch
	 * 
	 * @param from
	 * @param to
	 * @param max
	 * @return
	 */
	public static float changeAngle(float from, float to, float max) {
		float f = MathHelper.wrapDegrees(to - from);
		if (f > max) {
			f = max;
		}

		if (f < -max) {
			f = -max;
		}

		float g = from + f;
		if (g < 0.0F) {
			g += 360.0F;
		} else if (g > 360.0F) {
			g -= 360.0F;
		}

		return g;
	}

	/**
	 * Look to target -> float[] look = RotationUtils.lookAtEntity(values):
	 * 
	 * @param targetEntity
	 * @param maxYawChange
	 * @param maxPitchChange
	 */
	public static float[] lookAtEntity(Entity targetEntity, float maxYawChange,
			float maxPitchChange) {
		double d = targetEntity.getX() - Helper.getPlayer().getX();
		double e = targetEntity.getZ() - Helper.getPlayer().getZ();
		double g;
		if (targetEntity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) targetEntity;
			g = livingEntity.getEyeY() - Helper.getPlayer().getEyeY();
		} else {
			g = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0D
					- Helper.getPlayer().getEyeY();
		}

		double h = (double) MathHelper.sqrt(d * d + e * e);
		float i = (float) (MathHelper.atan2(e, d) * 57.2957763671875D) - 90.0F;
		float j = (float) (-(MathHelper.atan2(g, h) * 57.2957763671875D));
		float yaw = Helper.getPlayer().yaw;
		float pitch = Helper.getPlayer().pitch;
		pitch = limitAngleChange(pitch, j, maxPitchChange);
		yaw = limitAngleChange(yaw, i, maxYawChange);
		return new float[] { yaw, pitch };
	}

	/**
	 * Look to target with Vec3d component 
	 * @param targetEntity
	 * @param maxYawChange
	 * @param maxPitchChange
	 * @return
	 */
	public static float[] lookAtVecPos(Vec3d targetEntity, float maxYawChange,
			float maxPitchChange) {
		double d = targetEntity.getX() + 0.5 - Helper.getPlayer().getX();
		double g = targetEntity.getY() - Helper.getPlayer().getY();
		double e = targetEntity.getZ() + 0.5 - Helper.getPlayer().getZ();

		double h = (double) MathHelper.sqrt(d * d + e * e);
		float i = (float) (MathHelper.atan2(e, d) * 57.2957763671875D) - 90.0F;
		float j = (float) (-(MathHelper.atan2(g, h) * 57.2957763671875D));
		float yaw = Helper.getPlayer().yaw;
		float pitch = Helper.getPlayer().pitch;
		pitch = updateAngle(pitch, j, maxPitchChange);
		yaw = updateAngle(yaw, i, maxYawChange);
		return new float[] {yaw, pitch};
	}
	
	private static float updateAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
		float f = MathHelper.wrapDegrees(newAngle - oldAngle);
		if (f > maxChangeInAngle) {
			f = maxChangeInAngle;
		}

		if (f < -maxChangeInAngle) {
			f = -maxChangeInAngle;
		}

		return oldAngle + f;
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static float limitAngleChange(final float currentRotation, final float targetRotation, final float turnSpeed) {
		final float diff = RotationUtils.getAngleDifference(targetRotation, currentRotation);

		return currentRotation + (diff > turnSpeed ? turnSpeed : Math.max(diff, -turnSpeed));
	}

	public static float[] getEntityBox(Entity entity) {
		return getEntityBox(entity, 6.5F);
	}

	public static float getYawDifference(float currentYaw, float neededYaw) {
		float yawDifference = neededYaw - currentYaw;
		if (yawDifference > 180)
			yawDifference = -((360F - neededYaw) + currentYaw);
		else if (yawDifference < -180)
			yawDifference = ((360F - currentYaw) + neededYaw);

		return yawDifference;
	}

	/**
	 * @return Maximum/minimum rotation leniency allowed to still be considered
	 *         'inside' of a given entity.
	 */
	public static float[] getEntityBox(Entity entity, float distance) {
		float distanceRatio = distance / Helper.getPlayer().distanceTo(entity);
		float entitySize = 4.8F;
		return new float[] { distanceRatio * entity.getWidth() * entitySize,
				distanceRatio * entity.getHeight() * entitySize };
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

}
