package org.infinity.utils.entity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.rotation.RotationUtil;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {

	// Set target -> Entity entity -> to update method -> entity =
	// EntityUtil.setTarget(values);
	public static Entity setTarget(double range, double fov, boolean players, boolean friends, boolean invisibles,
			boolean mobs, boolean animals, boolean throughWalls) {
		Entity entity = null;
		float maxDist = (float) range;
		for (Entity e : getTargets(fov, players, friends, invisibles, mobs, animals, throughWalls)) {
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

	public static List<Entity> getTargets(double fov, boolean players, boolean friends, boolean invisibles,
			boolean mobs, boolean animals, boolean throughWalls) {
		return StreamSupport.stream(Helper.minecraftClient.world.getEntities().spliterator(), false).filter(
				entity -> isCombatTarget(entity, fov, players, friends, invisibles, mobs, animals, throughWalls))
				.collect(Collectors.toList());
	}

	/**
	 * for render
	 * 
	 * @param players
	 * @param invisibles
	 * @param mobs
	 * @param animals
	 * @return
	 */
	public static List<Entity> getRenderTargets(boolean players, boolean friends, boolean invisibles, boolean mobs,
			boolean animals) {
		return StreamSupport.stream(Helper.minecraftClient.world.getEntities().spliterator(), false)
				.filter(entity -> isTarget(entity, players, friends, invisibles, mobs, animals))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param entity
	 * @param fov
	 * @param players
	 * @param friends
	 * @param invisibles
	 * @param mobs
	 * @param animals
	 * @return
	 */
	public static boolean isCombatTarget(Entity entity, double fov, boolean players, boolean friends,
			boolean invisibles, boolean mobs, boolean animals, boolean throughWalls) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer() || entity instanceof ArmorStandEntity)
			return false;

		if (!RotationUtil.isInFOV(entity, fov))
			return false;

		if (!invisibles && entity.isInvisible())
			return false;

		if (!throughWalls && !Helper.getPlayer().canSee(entity))
			return false;

		boolean isFriend = InfMain.getFriend().getFriendList().contains(entity.getEntityName());

		if (!friends && isFriend)
			return false;

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

	/**
	 * @param entity
	 * @param players
	 * @param invisibles
	 * @param mobs
	 * @param animals
	 * @return
	 */
	public static boolean isTarget(Entity entity, boolean players, boolean friends, boolean invisibles, boolean mobs,
			boolean animals) {
		if (!(entity instanceof LivingEntity) || entity == Helper.getPlayer() || entity instanceof ArmorStandEntity)
			return false;

		boolean isFriend = InfMain.getFriend().getFriendList().contains(entity.getEntityName());

		if (!friends && isFriend)
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
		float tickDelta = 1.0F;
		Entity entity = Helper.minecraftClient.getCameraEntity();
		if (entity != null) {
			if (Helper.minecraftClient.world != null) {
				Helper.minecraftClient.getProfiler().push("pick");
				double d = reachDistance;
				Helper.minecraftClient.crosshairTarget = entity.raycast(d, tickDelta, false);
				Vec3d vec3d = entity.getCameraPosVec(tickDelta);
				double e = d;

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
						}
					}
				}
				Helper.minecraftClient.getProfiler().pop();
			}
		}
	}

	public static boolean placeBlock(Hand hand, BlockPos pos, boolean airPlace) {
		if (!Helper.getWorld().getBlockState(pos).getMaterial().isReplaceable())
			return false;

		Vec3d hitVec = null;
		BlockPos neighbor = null;
		Direction side2 = null;
		for (Direction side : Direction.values()) {
			neighbor = pos.offset(side);
			side2 = side.getOpposite();

			if (Helper.getWorld().getBlockState(neighbor).isAir()) {
				neighbor = null;
				side2 = null;
				continue;
			}

			hitVec = new Vec3d(neighbor.getX(), neighbor.getY(), neighbor.getZ()).add(0.5, 0.5, 0.5)
					.add(new Vec3d(side2.getUnitVector()).multiply(0.5));
			break;
		}

		if (airPlace && hitVec == null)
			hitVec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		else if (!airPlace && hitVec == null)
			return false;
		if (neighbor == null)
			neighbor = pos;
		if (side2 == null)
			side2 = Direction.UP;

		if (hitVec != null) {
			Helper.minecraftClient.interactionManager.interactBlock(Helper.getPlayer(), Helper.minecraftClient.world,
					hand, new BlockHitResult(hitVec, side2, neighbor, false));
			Helper.getPlayer().swingHand(hand);
		}

		return true;
	}

	/**
	 * Render color
	 * 
	 * @param entity
	 * @param player
	 * @param mobs
	 * @param animals
	 * @return
	 */
	public static int getEntitiesColor(Entity entity, int players, int friends, int mobs, int animals) {
		int color = -1;
		if (entity instanceof PlayerEntity)
			color = players;
		if (InfMain.getFriend().check(entity.getEntityName()))
			color = friends;
		if (isMonster(entity))
			color = mobs;
		if (isAnimal(entity))
			color = animals;
		return color;
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

	public static float distanceToBlock(BlockPos pos) {
		float f = (float) (Helper.getPlayer().getX() - pos.getX());
		float g = (float) (Helper.getPlayer().getY() - pos.getY());
		float h = (float) (Helper.getPlayer().getZ() - pos.getZ());
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public static void setStepHeight(float height) {
		Helper.getPlayer().stepHeight = height;
	}

	public static Vec3d getRenderPos(Entity e) {
		return Helper.minecraftClient.currentScreen != null && Helper.minecraftClient.currentScreen.isPauseScreen()
				? e.getPos().add(0, e.getHeight(), 0)
				: new Vec3d(e.lastRenderX + (e.getX() - e.lastRenderX) * Helper.minecraftClient.getTickDelta(),
						(e.lastRenderY + (e.getY() - e.lastRenderY) * Helper.minecraftClient.getTickDelta())
								+ e.getHeight(),
						e.lastRenderZ + (e.getZ() - e.lastRenderZ) * Helper.minecraftClient.getTickDelta());
	}

	public static boolean isOnGround(double height) {
		if (!Helper.minecraftClient.world.isSpaceEmpty(Helper.getPlayer(),
				Helper.getPlayer().getBoundingBox().offset(0.0D, -height, 0.0D)))
			return true;
		return false;
	}

}
