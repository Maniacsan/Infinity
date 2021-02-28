package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IPlayerPositionLookS2CPacket;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attack entities on range", key = GLFW.GLFW_KEY_R, name = "KillAura", visible = true)
public class KillAura extends Module {

	private Settings method = new Settings(this, "Method", "PRE", new ArrayList<>(Arrays.asList("PRE", "POST")));
	private Settings players = new Settings(this, "Players", true);
	private Settings invisibles = new Settings(this, "Invisibles", true);
	private Settings mobs = new Settings(this, "Mobs", true);
	private Settings animals = new Settings(this, "Animals", true);
	private Settings rayCast = new Settings(this, "RayCast", false);
	private Settings noSwing = new Settings(this, "No Swing", false);
	private Settings coolDown = new Settings(this, "CoolDown", true);
	private Settings maxSpeed = new Settings(this, "Max Speed", 80.0D, 0.0D, 180.0D);
	private Settings minSpeed = new Settings(this, "Min Speed", 80.0D, 0.0D, 180.0D);
	private Settings range = new Settings(this, "Range", 4.0D, 0.1D, 6.0D);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D);
	// target
	public static Entity target;

	//
	private static Random random = new Random();

	// rotation timers
	private long lastMS = -1;
	private double y;
	private double x;
	private double x1 = random.nextDouble();
	private double y1 = random.nextDouble();
	private double z1 = random.nextDouble();

	private boolean hitting;

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {
		target = null;
		super.onDisable();
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (target == null)
			return;
		if (random.nextGaussian() > 0.8D)
			x1 = Math.random();
		if (random.nextGaussian() > 0.8D)
			y1 = Math.random();
		if (random.nextGaussian() > 0.8D)
			z1 = Math.random();
	}

	@Override
	public void onPlayerTick() {
		if (target == null)
			return;
		double timeToSwitch = RandomUtils.nextDouble(96D, 950.232D);
		double verticalSwitch = RandomUtils.nextDouble(0.04343D, 16.0342742D);
		x = Math.random() * (12 - 1) + 1;
		if ((getCurrentTime() - lastMS) >= timeToSwitch) {
			y = verticalSwitch;
			lastMS = getCurrentTime();
		}

		float speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
				+ minSpeed.getCurrentValueDouble());
		float[] lookEntity = RotationUtils.lookAtEntity(target, speed, speed);

		// raycasting rotation
		if (rayCast.isToggle())
			EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), lookEntity[0], lookEntity[1]);
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), players.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle());
			if (target == null)
				return;

			if (method.getCurrentMode().equalsIgnoreCase("PRE")) {
				getYawBoxBorders(target,
						(float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
								+ minSpeed.getCurrentValueDouble()));
				attack();
			}
		} else if (event.getType().equals(EventType.POST)) {
			if (method.getCurrentMode().equalsIgnoreCase("POST")) {
				getYawBoxBorders(target,
						(float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
								+ minSpeed.getCurrentValueDouble()));
				attack();
			}
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.RECIEVE)) {
			if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
				PlayerPositionLookS2CPacket serverLook = (PlayerPositionLookS2CPacket) event.getPacket();

				if (target != null) {
					((IPlayerPositionLookS2CPacket) serverLook).setYaw(Helper.getPlayer().yaw);
					((IPlayerPositionLookS2CPacket) serverLook).setPitch(Helper.getPlayer().pitch);
				}
			}
		}
	}

	public void attack() {
		if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
				: timer.hasReached(1000 / aps.getCurrentValueDouble())) {
			hitting = true;
			Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
			EntityUtil.swing(!noSwing.isToggle());
			timer.reset();
		}
	}

	private float[] predictRotation(final Vec3d vec) {
		final Vec3d eyesPos = new Vec3d(Helper.getPlayer().getX(),
				Helper.getPlayer().getBoundingBox().minY + Helper.getPlayer().getStandingEyeHeight(),
				Helper.getPlayer().getZ());

		final double diffX = vec.x - eyesPos.x;
		final double diffY = vec.y - eyesPos.y;
		final double diffZ = vec.z - eyesPos.z;

		return new float[] { MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F), MathHelper
				.wrapDegrees((float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))) };
	}

	private void getYawBoxBorders(Entity entity, float yawSpeed) {
		Box bb = entity.getBoundingBox();
		final Vec3d randomVec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * x1 * 0.8,
				bb.minY + (bb.maxY - bb.minY) * y1 * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z1 * 0.8);
		float[] randomRotation = predictRotation(randomVec);
		float[] rotations = RotationUtils.lookAtEntity(entity, yawSpeed, yawSpeed);
		float[] rotationCaps = RotationUtils.getEntityBox(entity);
		float yawDifference = RotationUtils.getYawDifference(Helper.getPlayer().yaw, rotations[0]);

		if (hitting) {
			Helper.getPlayer().yaw = rotations[0];
			hitting = false;
		}

		if (yawDifference > rotationCaps[0] || yawDifference < -rotationCaps[0]) {
			if (yawDifference < 0) {
				Helper.getPlayer().yaw += yawDifference + rotationCaps[0] - 0.57676D;
			} else if (yawDifference > 0) {
				Helper.getPlayer().yaw += yawDifference - rotationCaps[0] + 0.57676D;
			}
		} else {
			float randomYaw = RotationUtils.limitAngleChange(Helper.getPlayer().yaw, randomRotation[0], yawSpeed);
			float randomPitch = RotationUtils.limitAngleChange(Helper.getPlayer().pitch, randomRotation[1], yawSpeed);
			Helper.getPlayer().yaw = (float) (randomYaw);
			Helper.getPlayer().pitch = (float) (randomPitch);
		}

	}

	// timer to randoming
	private long getCurrentTime() {
		return System.nanoTime() / 1000000L;
	}

}
