package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.glfw.GLFW;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

import me.infinity.event.MotionEvent;
import me.infinity.event.PacketEvent;
import me.infinity.event.RotationEvent;
import me.infinity.event.TickEvent;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.PacketUtil;
import me.infinity.utils.TimeHelper;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.entity.PlayerSend;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Attack entities on range", key = GLFW.GLFW_KEY_R, name = "KillAura", visible = true)
public class KillAura extends Module {

	private Settings rotation = new Settings(this, "Rotation", "Focus",
			new ArrayList<>(Arrays.asList("Smash", "Focus")), () -> true);
	private Settings method = new Settings(this, "Method", "PRE", new ArrayList<>(Arrays.asList("PRE", "POST")),
			() -> true);
	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	// raycasting target
	private Settings rayCast = new Settings(this, "RayCast", false, () -> true);

	// matrix packet rotation strafing check
	private Settings badStrafe = new Settings(this, "Bad Strafe", false, () -> true);

	private Settings noSwing = new Settings(this, "No Swing", false, () -> true);
	private Settings coolDown = new Settings(this, "CoolDown", true, () -> true);

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);
	private Settings maxSpeed = new Settings(this, "Max Speed", 135.0D, 0.0D, 180.0D, () -> true);
	private Settings minSpeed = new Settings(this, "Min Speed", 80.0D, 0.0D, 180.0D, () -> true);
	private Settings range = new Settings(this, "Range", 4.0D, 0.1D, 6.0D, () -> true);
	private Settings aps = new Settings(this, "APS", 1.8D, 0.1D, 15.0D, () -> Boolean.valueOf(!coolDown.isToggle()));

	// target
	public static Entity target;

	//
	private static Random random = new Random();

	private float prevYaw;
	private float prevPitch;

	// rotations
	private float[] focus;
	private float[] smash;

	private float speed;

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

		if (target == null)
			return;

		// set speed with minecraft tick
		speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
				+ minSpeed.getCurrentValueDouble());

		// set rotation
		focus = focus();

		smash = rotation(target, Helper.minecraftClient.options.mouseSensitivity, speed);
	}

	@Override
	public void onPlayerTick() {
		if (target == null)
			return;

		if (rayCast.isToggle()) {
			if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), focus[0], focus[1]);
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), smash[0], smash[1]);
			}
		}

		if (badStrafe.isToggle()) {
			if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				MoveUtil.silentStrafe(focus[0]);
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				MoveUtil.silentStrafe(smash[0]);
			}
		}

	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		if (event.getType().equals(EventType.PRE)) {
			prevYaw = Helper.getPlayer().yaw;
			prevPitch = Helper.getPlayer().pitch;
			target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), fov.getCurrentValueDouble(),
					players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle());
			if (target == null)
				return;
			
			if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				PlayerSend.setRotation(focus[0], focus[1]);
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				PlayerSend.setRotation(smash[0], smash[1]);
			}
		

			if (method.getCurrentMode().equalsIgnoreCase("PRE")) {
				attack();
			}
		} else if (event.getType().equals(EventType.POST)) {
			if (target == null)
				return;
			
			if (method.getCurrentMode().equalsIgnoreCase("POST")) {
				attack();
			}
			
			
		}
	}

	@EventTarget
	public void onPacket(PacketEvent event) {

		if (target != null) {
			if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				if (smash[1] < 90 || smash[1] > -90) {
					PacketUtil.setRotation(event, smash[0], smash[1]);
				}
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				PacketUtil.setRotation(event, focus[0], focus[1]);	
				}
			PacketUtil.cancelServerRotation(event);
		}

	}

	@EventTarget
	public void onRotation(RotationEvent event) {
		if (target == null)
			return;

		if (rayCast.isToggle()) {
			if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				if (smash[1] < 90 || smash[1] > -90) {
					event.setYaw(smash[0]);
					event.setPitch(smash[1]);
				}
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				event.setYaw(focus[0]);
				event.setPitch(focus[1]);
			}
		}
		event.cancel();
	}

	public void attack() {
		if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
				: timer.hasReached(1000 / aps.getCurrentValueDouble())) {
			double x = Helper.getPlayer().getX();
			double y = Helper.getPlayer().getY();
			double z = Helper.getPlayer().getZ();
			Helper.minecraftClient.interactionManager.attackEntity(Helper.getPlayer(), target);
			PlayerSend.setPosition(x, y + 0.0625, z, true);
			PlayerSend.setPosition(x, y, z, false);
			PlayerSend.setPosition(x, y + 1.1E-5, z, false);
			PlayerSend.setPosition(x, y, z, false);
			EntityUtil.swing(!noSwing.isToggle());
			timer.reset();
		}
	}

	public float[] focus() {
		float[] toCenter = RotationUtils.lookAtEntity(target, speed, speed);
		float sens = (float) (Helper.minecraftClient.options.mouseSensitivity);
		float f = (float) (sens * 0.6F + 0.2F);
		float gcd = f * f * f * 1.2F;
		
		float diffYaw = toCenter[0] - prevYaw;
		float diffPitch = toCenter[1] - prevPitch;
		
		diffYaw -= diffYaw % gcd;
		toCenter[0] = prevYaw + diffYaw;
		
		diffPitch -= diffPitch % gcd;
		toCenter[1] = prevPitch + diffPitch;
		
		return new float[] { toCenter[0], toCenter[1] };
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
			yaw = randomYaw;
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
