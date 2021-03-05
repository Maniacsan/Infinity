package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.mixin.IPlayerMoveC2SPacket;
import me.infinity.mixin.IPlayerPositionLookS2CPacket;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import me.infinity.utils.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attack entities on range", key = GLFW.GLFW_KEY_R, name = "KillAura", visible = true)
public class KillAura extends Module {

	private Settings method = new Settings(this, "Method", "PRE", new ArrayList<>(Arrays.asList("PRE", "POST")), true);
	private Settings players = new Settings(this, "Players", true, true);
	private Settings invisibles = new Settings(this, "Invisibles", true, true);
	private Settings mobs = new Settings(this, "Mobs", true, true);
	private Settings animals = new Settings(this, "Animals", true, true);
	private Settings rayCast = new Settings(this, "RayCast", false, true);
	private Settings noSwing = new Settings(this, "No Swing", false, true);
	private Settings coolDown = new Settings(this, "CoolDown", true, true);
	private Settings maxSpeed = new Settings(this, "Max Speed", 80.0D, 0.0D, 180.0D, true);
	private Settings minSpeed = new Settings(this, "Min Speed", 80.0D, 0.0D, 180.0D, true);
	private Settings range = new Settings(this, "Range", 4.0D, 0.1D, 6.0D, true);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D, true);
	// target
	public static Entity target;

	//
	private static Random random = new Random();

	private float prevYaw;
	private float prevPitch;

	// rotation timers
	private double x1 = random.nextDouble();
	private double y1 = random.nextDouble();
	private double z1 = random.nextDouble();

	private TimeHelper timer = new TimeHelper();

	@Override
	public void onDisable() {
		target = null;
		super.onDisable();
	}

	@EventTarget
	public void onTick(TickEvent event) {
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

		float speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
				+ minSpeed.getCurrentValueDouble());
		float[] lookEntity = rotation(target, Helper.minecraftClient.options.mouseSensitivity, speed);

		// raycasting rotation
		if (rayCast.isToggle())
			EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), lookEntity[0], lookEntity[1]);
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			prevYaw = Helper.getPlayer().yaw;
			prevPitch = Helper.getPlayer().pitch;
			target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), players.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle());
			if (target == null)
				return;

			rotationTick();

			if (method.getCurrentMode().equalsIgnoreCase("PRE")) {
				attack();
			}
		} else if (event.getType().equals(EventType.POST)) {
			if (method.getCurrentMode().equalsIgnoreCase("POST")) {
				attack();
			}

			// spoofing rotation camera
			Helper.getPlayer().yaw = prevYaw;
			Helper.getPlayer().pitch = prevPitch;
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {
		if (event.getType().equals(EventType.SEND)) {
			float speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
					+ minSpeed.getCurrentValueDouble());
			float[] lookEntity = rotation(target, Helper.minecraftClient.options.mouseSensitivity, speed);
			if (event.getPacket() instanceof PlayerMoveC2SPacket) {
				PlayerMoveC2SPacket cp = (PlayerMoveC2SPacket) event.getPacket();
				if (!Float.isNaN(lookEntity[0]) || !Float.isNaN(lookEntity[1]) || lookEntity[1] < 90
						|| lookEntity[1] > -90) {
					((IPlayerMoveC2SPacket) cp).setYaw(lookEntity[0]);
					((IPlayerMoveC2SPacket) cp).setPitch(lookEntity[1]);
				}
			}
		}
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
			Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
			EntityUtil.swing(!noSwing.isToggle());
			timer.reset();
		}
	}

	private void rotationTick() {
		float speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
				+ minSpeed.getCurrentValueDouble());
		float[] lookEntity = rotation(target, Helper.minecraftClient.options.mouseSensitivity, speed);
		if (Float.isNaN(lookEntity[0]) || Float.isNaN(lookEntity[1]) || lookEntity[1] > 90 || lookEntity[1] < -90)
			return;
		Helper.getPlayer().yaw = lookEntity[0];
		Helper.getPlayer().pitch = lookEntity[1];
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

	private float[] rotation(Entity entity, double sensitivy, float speed) {
		float yaw = Helper.getPlayer().yaw, pitch = Helper.getPlayer().pitch;
		Box bb = entity.getBoundingBox();
		float lx = 0, ly = 0, lz = 0;
		lx = RotationUtils.limitAngleChange(lx, (float) x1, speed);
		ly = RotationUtils.limitAngleChange(ly, (float) y1, speed);
		lz = RotationUtils.limitAngleChange(lz, (float) z1, speed);

		final Vec3d randomVec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * lx * 0.8,
				bb.minY + (bb.maxY - bb.minY) * ly * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * lz * 0.8);
		float[] randomRotation = predictRotation(randomVec);
		float[] rotations = RotationUtils.lookAtEntity(entity, speed, speed);
		float[] hitbox = RotationUtils.getEntityBox(entity);
		float yawDifference = RotationUtils.getYawDifference(Helper.getPlayer().yaw, rotations[0]);

		if (yawDifference > hitbox[0] || yawDifference < -hitbox[0]) {
			if (yawDifference < 0) {
				yaw += yawDifference + hitbox[0];
			} else if (yawDifference > 0) {
				yaw += yawDifference - hitbox[0];
			}
		} else {
			float randomYaw = RotationUtils.limitAngleChange(Helper.getPlayer().yaw, randomRotation[0], speed);
			float randomPitch = RotationUtils.limitAngleChange(Helper.getPlayer().pitch, randomRotation[1], speed);

			pitch = randomPitch;
		}

		// matrix sensitive/acceleration check bypass
		float f = (float) (sensitivy * 0.6F + 0.2F);
		float gcd = f * f * f * 1.2F;

		yaw -= yaw % gcd;
		pitch -= pitch % gcd;

		return new float[] { yaw, pitch };

	}

}
