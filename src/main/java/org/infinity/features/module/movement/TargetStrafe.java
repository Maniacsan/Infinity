package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.features.module.combat.KillAura;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.MoveUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Category.MOVEMENT, desc = "Whirls in a circle of entity", key = -2, name = "TargetStrafe", visible = true)
public class TargetStrafe extends Module {

	private Setting mode = new Setting(this, "Mode", "Basic", new ArrayList<>(Arrays.asList("Basic", "Scroll")));

	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", true);

	private Setting distance = new Setting(this, "Entity Distance", 7.0D, 6.1D, 15.0D);

	private Setting radius = new Setting(this, "Strafing radius", 1.9D, 0.0D, 6.0D);
	private Setting speed = new Setting(this, "Speed", 0.24D, 0.0D, 1.0D);

	private Setting scrollSpeed = new Setting(this, "Speed to Entity", 0.26D, 0.0D, 1.0D)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Scroll"));

	private Setting damageBoost = new Setting(this, "Damage Boost", false);
	private Setting boost = new Setting(this, "Boost Value", 0.09D, 0.0D, 0.8D)
			.setVisible(() -> damageBoost.isToggle());

	private Entity target;

	private double direction = 1;

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		setSuffix(mode.getCurrentMode());

		target = EntityUtil.setTarget(distance.getCurrentValueDouble(), 360, players.isToggle(), friends.isToggle(),
				invisibles.isToggle(), mobs.isToggle(), animals.isToggle(), true);

		if (target == null)
			return;

		if (Helper.getPlayer().isOnGround())
			Helper.getPlayer().jump();

		if (Helper.minecraftClient.options.keyLeft.isPressed()) {
			direction = 1;
		} else if (Helper.minecraftClient.options.keyRight.isPressed()) {
			direction = -1;
		}

		if (Helper.getPlayer().horizontalCollision)
			direction = direction == 1 ? -1 : 1;

		double speed = damageBoost.isToggle() && Helper.getPlayer().hurtTime != 0
				? this.speed.getCurrentValueDouble() + boost.getCurrentValueDouble()
				: this.speed.getCurrentValueDouble();

		float yaw = getNormalizeYaw(target);

		if (mode.getCurrentMode().equalsIgnoreCase("Basic")) {
			if (Helper.getPlayer().distanceTo(target) > radius.getCurrentValueDouble())
				getBasic(yaw, speed, 1, direction);
			else
				getBasic(yaw, speed, 0, direction);

		} else if (mode.getCurrentMode().equalsIgnoreCase("Scroll"))
			getScroll(target, speed);
	}

	private void getScroll(Entity target, double speed) {
		double c1 = (Helper.getPlayer().getX() - target.getX())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));
		double s1 = (Helper.getPlayer().getZ() - target.getZ())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));

		double x = speed * s1 * direction - scrollSpeed.getCurrentValueDouble() * speed * c1;
		double z = -speed * c1 * direction - scrollSpeed.getCurrentValueDouble() * speed * s1;

		MoveUtil.setHVelocity(x, z);
	}

	private void getBasic(float yaw, double speed, double forward, double direction) {
		if (forward != 0.0D) {
			if (direction > 0.0D) {
				yaw += (float) (forward > 0.0D ? -45 : 45);
			} else if (direction < 0.0D) {
				yaw += (float) (forward > 0.0D ? 45 : -45);
			}
			direction = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		Helper.getPlayer().bodyYaw = yaw;
		Helper.getPlayer().headYaw = yaw;

		double x = forward * speed * Math.cos(Math.toRadians((yaw + 90.0F)))
				+ direction * speed * Math.sin(Math.toRadians((yaw + 90.0F)));
		double z = forward * speed * Math.sin(Math.toRadians((yaw + 90.0F)))
				- direction * speed * Math.cos(Math.toRadians((yaw + 90.0F)));

		MoveUtil.setHVelocity(x, z);
		Helper.getPlayer().getVelocity().subtract(0, -0.02, 0);
	}

	public static float getNormalizeYaw(Entity entity) {
		float yaw = RotationUtil.lookAtEntity(entity)[0];
		float f = (float) (Helper.minecraftClient.options.mouseSensitivity * 0.6F + 0.2F);
		float gcd = f * f * f * 1.2F;

		yaw -= yaw % gcd;

		KillAura killAura = ((KillAura) InfMain.getModuleManager().getModuleByClass(KillAura.class));
		if (killAura.isEnabled() && KillAura.target != null) {
			if (killAura.rotation.getCurrentMode().equalsIgnoreCase("Focus")) {
				yaw = killAura.focus[0];
			} else if (killAura.rotation.getCurrentMode().equalsIgnoreCase("Smash")) {
				yaw = killAura.smash[0];
			}
		}

		return yaw;
	}

}
