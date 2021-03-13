package me.infinity.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import me.infinity.InfMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {

	// Set target -> Entity entity -> to update method -> entity =
	// EntityUtil.setTarget(values);
	public static Entity setTarget(double range, double fov, boolean players, boolean friends, boolean invisibles,
			boolean mobs, boolean animals) {
		Entity entity = null;
		float maxDist = (float) range;
		for (Entity e : getTargets(fov, players, friends, invisibles, mobs, animals)) {
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

	public static Entity setRenderTarget(boolean players, boolean invisibles, boolean mobs, boolean animals) {
		Entity entity = null;
		for (Entity target : Helper.minecraftClient.world.getEntities()) {
			if (isTarget(target, players, invisibles, mobs, animals)) {
				entity = target;
			}
		}
		return entity;
	}

	public static List<Entity> getTargets(double fov, boolean players, boolean friends, boolean invisibles,
			boolean mobs, boolean animals) {
		return StreamSupport.stream(Helper.minecraftClient.world.getEntities().spliterator(), false)
				.filter(entity -> isCombatTarget(entity, fov, players, friends, invisibles, mobs, animals))
				.collect(Collectors.toList());
	}

	public static boolean isCombatTarget(Entity entity, double fov, boolean players, boolean friends,
			boolean invisibles, boolean mobs, boolean animals) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer() || entity instanceof ArmorStandEntity)
			return false;

		if (!RotationUtils.isInFOV(entity, fov))
			return false;

		if (!friends && InfMain.getFriend().check(entity.getEntityName()))
			return false;

		if (invisibles && entity.isInvisible())
			return true;
		if (players && entity instanceof PlayerEntity)
			return true;
		if (mobs && isMonster(entity))
			return true;
		if (animals && isAnimal(entity))
			return true;

		// entity dead check
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0)
			return false;

		return false;
	}

	public static boolean isTarget(Entity entity, boolean players, boolean invisibles, boolean mobs, boolean animals) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer() || entity instanceof ArmorStandEntity)
			return false;

		if (invisibles && entity.isInvisible())
			return true;
		if (players && entity instanceof PlayerEntity)
			return true;
		if (mobs && isMonster(entity))
			return true;
		if (animals && isAnimal(entity))
			return true;

		// entity dead check
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0)
			return false;

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
	public static Entity updateTargetRaycast(double reachDistance, float yaw, float pitch) {
		float tickDelta = 1.0F;
		Entity entity = Helper.minecraftClient.getCameraEntity();
		if (entity != null) {
			if (Helper.minecraftClient.world != null) {
				Helper.minecraftClient.getProfiler().push("pick");
				double d = reachDistance;
				Helper.minecraftClient.crosshairTarget = entity.raycast(d, tickDelta, false);
				Vec3d vec3d = entity.getCameraPosVec(tickDelta);
				double e = d;

				Entity target = null;

				if (Helper.minecraftClient.crosshairTarget != null) {
					e = Helper.minecraftClient.crosshairTarget.getPos().squaredDistanceTo(vec3d);
				}

				e *= e;
				Vec3d vec3d2 = entity.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
				Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0D, 1.0D, 1.0D);
				EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> {
					return !entityx.isSpectator() && entityx.collides();
				}, e);
				if (entityHitResult != null) {
					Entity entity2 = entityHitResult.getEntity();
					Vec3d vec3d4 = entityHitResult.getPos();
					double g = vec3d.squaredDistanceTo(vec3d4);
					if (g < e || Helper.minecraftClient.crosshairTarget == null) {
						Helper.minecraftClient.crosshairTarget = entityHitResult;
						if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
							target = entity2;
							return target;
						}
					}
				}
				Helper.minecraftClient.getProfiler().pop();
			}
		}
		return null;
	}

	// raycast to block rotation
	public static void updateBlockRaycast(HitResult crosshairTarget, float yaw, float pitch) {
		float tickDelta = Helper.minecraftClient.getTickDelta();
		Entity entity = Helper.minecraftClient.getCameraEntity();
		if (entity != null) {
			if (Helper.minecraftClient.world != null) {
				Helper.minecraftClient.getProfiler().push("pick");
				double d = Helper.minecraftClient.interactionManager.getReachDistance();
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
					Vec3d vec3d4 = entityHitResult.getPos();
					double g = vec3d.squaredDistanceTo(vec3d4);
					if (bl && g > 9.0D) {
						Helper.minecraftClient.crosshairTarget = BlockHitResult.createMissed(vec3d4,
								Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
						crosshairTarget = BlockHitResult.createMissed(vec3d4,
								Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
					} else if (g < e || Helper.minecraftClient.crosshairTarget == null) {
						Helper.minecraftClient.crosshairTarget = entityHitResult;
						crosshairTarget = entityHitResult;
					}
				}

				Helper.minecraftClient.getProfiler().pop();
			}
		}
	}

	public static boolean placeBlock(Hand hand, BlockPos pos) {
		if (!Helper.minecraftClient.world.getBlockState(pos).getMaterial().isReplaceable())
			return false;

		Vec3d hitVec = null;
		BlockPos neighbor = null;
		Direction side2 = null;
		for (Direction side : Direction.values()) {
			neighbor = pos.offset(side);
			side2 = side.getOpposite();

			// check place block position on air
			if (Helper.minecraftClient.world.getBlockState(neighbor).isAir()) {
				neighbor = null;
				side2 = null;
				continue;
			}

			hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ()).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getUnitVector()).multiply(0.5));
			break;
		}

		if (neighbor == null)
			neighbor = pos;
		if (side2 == null)
			side2 = Direction.UP;
		if (hitVec == null)
			return false;

		if (hitVec != null) {
			Helper.minecraftClient.interactionManager.interactBlock(Helper.getPlayer(), Helper.minecraftClient.world,
					hand, new BlockHitResult(hitVec, side2, neighbor, false));
			Helper.getPlayer().swingHand(hand);
		}

		return true;
	}

	public static void swing(boolean animation) {
		if (animation) {
			Helper.getPlayer().swingHand(Hand.MAIN_HAND);
		} else {
			Helper.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
		}
	}

	public static BlockPos getPlayerPosFloor() {
		return new BlockPos(Math.floor(Helper.getPlayer().getX()), Math.floor(Helper.getPlayer().getY()),
				Math.floor(Helper.getPlayer().getZ()));
	}

	public static boolean checkActivePotion(StatusEffect effect) {
		if (Helper.getPlayer().getStatusEffect(effect) != null)
			return true;
		return false;
	}
}
