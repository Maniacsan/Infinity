package me.infinity.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {

	// Set target -> Entity entity -> to update method -> entity =
	// EntityUtil.setTarget(values);
	public static Entity setTarget(double range, boolean players, boolean invisibles, boolean mobs, boolean animals) {
		Entity entity = null;
		float maxDist = (float) range;
		for (Entity e : getTargets(players, invisibles, mobs, animals)) {
			if (e != null) {
				float currentDist = Helper.getPlayer().distanceTo(e);
				if (currentDist <= maxDist) {
					maxDist = currentDist;
					entity = e;
				}
			}
		}
		return entity;
	}

	public static List<Entity> getTargets(boolean players, boolean invisibles, boolean mobs, boolean animals) {
		return StreamSupport.stream(Helper.minecraftClient.world.getEntities().spliterator(), false)
				.filter(entity -> isTarget(entity, players, invisibles, mobs, animals)).collect(Collectors.toList());
	}

	public static boolean isTarget(Entity entity, boolean players, boolean invisibles, boolean mobs, boolean animals) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer())
			return false;

		if (invisibles && entity.isInvisible())
			return true;
		if (players && entity instanceof PlayerEntity)
			return true;
		if (mobs && isMonster(entity))
			return true;
		if (animals && isAnimal(entity))
			return true;

		return false;
	}

	public static boolean isAnimal(Entity e) {
		return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof WaterCreatureEntity
				|| e instanceof GolemEntity;
	}

	public static boolean isMonster(Entity e) {
		return e instanceof Monster;
	}

	// raycast entity
	public static void updateTargetRaycast(Entity target, double reachDistance, float yaw, float pitch) {
		float tickDelta = Helper.minecraftClient.getTickDelta();
		Entity entity = Helper.minecraftClient.getCameraEntity();
		if (entity != null) {
			if (Helper.minecraftClient.world != null) {
				Helper.minecraftClient.getProfiler().push("pick");
				double d = reachDistance;
				Helper.minecraftClient.crosshairTarget = entity.raycast(d, tickDelta, false);
				Vec3d vec3d = entity.getCameraPosVec(tickDelta);
				boolean bl = false;
				double e = d;

				e *= e;
				if (Helper.minecraftClient.crosshairTarget != null) {
					e = Helper.minecraftClient.crosshairTarget.getPos().squaredDistanceTo(vec3d);
				}

				Vec3d vec3d2 = RotationUtils.getRotationVec(yaw, pitch);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
				EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> {
					return !entityx.isSpectator() && entityx.collides();
				}, e);
				if (entityHitResult != null) {
					Entity entity2 = entityHitResult.getEntity();
					Vec3d vec3d4 = entityHitResult.getPos();
					double g = vec3d.squaredDistanceTo(vec3d4);
					if (bl && g > 9.0D) {
						Helper.minecraftClient.crosshairTarget = BlockHitResult.createMissed(vec3d4,
								Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
					} else if (g < e || Helper.minecraftClient.crosshairTarget == null) {
						Helper.minecraftClient.crosshairTarget = entityHitResult;
						if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
							target = entity2;
						}
					}
				}

				Helper.minecraftClient.getProfiler().pop();
			}
		}
	}

	public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
		return (new Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ))
				.add(getInterpolatedAmount(entity, ticks));
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
		return new Vec3d((entity.getX() - entity.lastRenderX) * x, (entity.getY() - entity.lastRenderY) * y,
				(entity.getZ() - entity.lastRenderZ) * z);
	}

	public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
		return getInterpolatedAmount(entity, ticks, ticks, ticks);
	}

	public static void swing(boolean animation) {
		if (animation) {
			Helper.getPlayer().swingHand(Hand.MAIN_HAND);
		} else {
			Helper.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
		}
	}
}
