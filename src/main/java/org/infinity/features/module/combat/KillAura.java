package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.infinity.event.MotionEvent;
import org.infinity.event.RotationEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.features.module.player.FakeLags;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.InvUtil;
import org.infinity.utils.MathAssist;
import org.infinity.utils.Timer;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Category.COMBAT, desc = "Attack entities on range", key = -2, name = "KillAura", visible = true)
public class KillAura extends Module {

	public Setting rotation = new Setting(this, "Rotation", "Focus",
			new ArrayList<>(Arrays.asList("Smash", "Focus", "Reset")));

	private Setting randomYaw = new Setting(this, "Random Yaw", 0.5D, 0.0D, 0.8D)
			.setVisible(() -> rotation.getCurrentMode().equalsIgnoreCase("Smash"));
	private Setting randomPitch = new Setting(this, "Random Pitch", 0.2D, 0.0D, 0.8D)
			.setVisible(() -> rotation.getCurrentMode().equalsIgnoreCase("Smash"));

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);
	private Setting throughWalls = new Setting(this, "Through Walls", true);

	private Setting fov = new Setting(this, "FOV", 240D, 0D, 360D);

	private Setting destroyShield = new Setting(this, "Destroy Shield (Axe)", true);

	private Setting releaseShield = new Setting(this, "Let go of the shield on impact", false);

	private Setting keepSprint = new Setting(this, "Keep Sprint", true);

	private Setting lockView = new Setting(this, "Look View", false);

	private Setting maxSpeed = new Setting(this, "Max Speed", 199.0D, 0.0D, 200.0D);
	private Setting minSpeed = new Setting(this, "Min Speed", 184.0D, 0.0D, 200.0D);

	// raycasting target
	private Setting rayCast = new Setting(this, "RayCast", true);

	private Setting noSwing = new Setting(this, "No Swing", false);
	private Setting coolDown = new Setting(this, "CoolDown", true);
	private Setting aps = new Setting(this, "APS", 1.8D, 0.1D, 15.0D).setVisible(() -> !coolDown.isToggle());

	private Setting range = new Setting(this, "Range", 3.7D, 0.1D, 6.0D);

	// target
	public static Entity target;

	//
	private static Random random = new Random();

	// rotations
	public float[] focus;
	public float[] smash;

	private float lastYaw = 999f;
	private float lastPitch = 999f;

	private int time;
	private float speed;

	// smash
	private double x1 = random.nextDouble();
	private double y1 = random.nextDouble();
	private double z1 = random.nextDouble();

	private int preSlot = -2;

	private Timer timer = new Timer();

	@Override
	public void onDisable() {
		target = null;
		time = 0;
		super.onDisable();
	}

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		setSuffix(rotation.getCurrentMode());

		target = EntityUtil.getTarget(range.getCurrentValueDouble(), fov.getCurrentValueDouble(), players.isToggle(),
				friends.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle(),
				throughWalls.isToggle());

		if (target == null)
			return;

		speed = (float) (Math.random() * (maxSpeed.getCurrentValueDouble() - minSpeed.getCurrentValueDouble())
				+ minSpeed.getCurrentValueDouble());

		focus = RotationUtil.lookAtEntity(target);
		smash = rotation(target, Helper.MC.options.mouseSensitivity, speed);

		// bypass matrix acceleration check
		focus[0] = (float) Math.round((focus[0] + MathAssist.random(-3D, 3D)) * 1000) / 1000;
		focus[1] = (float) Math.round((focus[1] + MathAssist.random(-3D, 3D)) * 1000) / 1000;

		smash = rotation(target, Helper.MC.options.mouseSensitivity, speed);
		smash[0] = RotationUtil.limitAngleChange(event.getYaw(), smash[0], speed);
		smash[1] = RotationUtil.limitAngleChange(event.getPitch(), smash[1], speed);

		if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
			event.setRotation(focus[0], focus[1], lockView.isToggle());
		} else if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
			event.setRotation(smash[0], smash[1], lockView.isToggle());
		} else if (rotation.getCurrentMode().equalsIgnoreCase("Reset")) {
			if (lastYaw != 999 || lastPitch != 999) {
				event.setRotation(lastYaw, lastPitch, lockView.isToggle());
			}
		}

		attack(event);

		if (random.nextGaussian() > randomYaw.getCurrentValueDouble())
			x1 = Math.random();
		if (random.nextGaussian() > randomPitch.getCurrentValueDouble())
			y1 = Math.random();
		if (random.nextGaussian() > randomYaw.getCurrentValueDouble())
			z1 = Math.random();

		if (time > 0) {
			time--;
		}

		if (time <= 0) {
			lastYaw = 999;
			lastPitch = 999;
		}

		if (rayCast.isToggle()) {
			if (rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), focus[0], focus[1]);
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				EntityUtil.updateTargetRaycast(target, range.getCurrentValueDouble(), smash[0], smash[1]);
			}
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
			} else if (rotation.getCurrentMode().equalsIgnoreCase("Reset")) {
				event.setYaw(lastYaw);
				event.setPitch(lastPitch);
			}
			event.cancel();
		}
	}

	private void destroyShield() {
		int slotAxe = InvUtil.findAxe();

		if (!destroyShield.isToggle())
			return;

		if (target instanceof PlayerEntity) {
			if (((PlayerEntity) target).isBlocking()) {
				if (slotAxe != -2) {
					preSlot = Helper.getPlayer().getInventory().selectedSlot;
					Helper.getPlayer().getInventory().selectedSlot = slotAxe;

					if (preSlot != -2) {
						(new Thread() {
							@Override
							public void run() {
								try {
									Thread.sleep(150);

									Helper.getPlayer().getInventory().selectedSlot = preSlot;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
		}
	}

	public void attack(MotionEvent event) {
		if (coolDown.isToggle() ? Helper.getPlayer().getAttackCooldownProgress(0.0f) >= 1
				: timer.hasReached(1000 / aps.getCurrentValueDouble())) {
			if (Criticals.fall(target)) {

				if (releaseShield.isToggle() && Helper.getPlayer().isBlocking())
					Helper.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
							new BlockPos(0, 0, 0), Direction.DOWN));

				destroyShield();

				if (rotation.getCurrentMode().equalsIgnoreCase("Reset")) {
					float[] matrix = RotationUtil.lookAtEntity(target);

					matrix[0] = RotationUtil.limitAngleChange(event.getYaw(), matrix[0], speed);
					matrix[1] = RotationUtil.limitAngleChange(event.getPitch(), matrix[1], speed);

					lastYaw = matrix[0];
					lastPitch = matrix[1];
					event.setRotation(matrix[0], matrix[1], lockView.isToggle());
					Helper.getPlayer().bodyYaw = matrix[0];
					Helper.getPlayer().headYaw = matrix[0];
					time = 4;
				}

				// fakeLags reset
				if (InfMain.getModuleManager().get(FakeLags.class).isEnabled())
					((FakeLags) InfMain.getModuleManager().get(FakeLags.class)).sendPackets();

				Helper.sendPacket(PlayerInteractEntityC2SPacket.attack(target, Helper.getPlayer().isSneaking()));
				EntityUtil.swing(!noSwing.isToggle());

				if (keepSprint.isToggle()) {
					if (Helper.getPlayer().fallDistance > 0.0F && !Helper.getPlayer().isOnGround()
							&& !Helper.getPlayer().isClimbing() && !Helper.getPlayer().isTouchingWater()
							&& !Helper.getPlayer().hasStatusEffect(StatusEffects.BLINDNESS)
							&& !Helper.getPlayer().hasVehicle() && target instanceof LivingEntity) {
						Helper.getPlayer().addCritParticles(target);
					}

					if (EnchantmentHelper.getAttackDamage(Helper.getPlayer().getMainHandStack(),
							((LivingEntity) target).getGroup()) > 0) {
						Helper.getPlayer().addEnchantedHitParticles(target);
					}

					Helper.getPlayer().resetLastAttackedTicks();
				} else {
					Helper.getPlayer().attack(target);
				}
				timer.reset();
				if (releaseShield.isToggle() && Helper.getPlayer().isBlocking())
					Helper.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND));

			}
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

	private float[] rotation(Entity entity, double sensitivy, float speed) {
		float yaw = Helper.getPlayer().getYaw(), pitch = Helper.getPlayer().getPitch();
		Box bb = entity.getBoundingBox();
		float lx = 0, ly = 0, lz = 0;
		lx = RotationUtil.limitAngleChange(lx, (float) x1, speed);
		ly = RotationUtil.limitAngleChange(ly, (float) y1, speed);
		lz = RotationUtil.limitAngleChange(lz, (float) z1, speed);

		final Vec3d randomVec = new Vec3d(bb.minX + (bb.maxX - bb.minX) * lx * randomYaw.getCurrentValueDouble(),
				bb.minY + ((bb.maxY / 2) - (bb.minY / 2))
						+ MathAssist.random(-randomPitch.getCurrentValueDouble(), randomPitch.getCurrentValueDouble()),
				bb.minZ + (bb.maxZ - bb.minZ) * lz * randomYaw.getCurrentValueDouble());
		float[] randomRotation = predictRotation(randomVec);

		yaw = randomRotation[0];
		pitch = (float) (randomRotation[1]);

		return new float[] { yaw, pitch };

	}

}
