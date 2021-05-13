package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.MoveUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;

@ModuleInfo(category = Category.MOVEMENT, desc = "Whirls in a circle of entity", key = -2, name = "TargetStrafe", visible = true)
public class TargetStrafe extends Module {

	private Setting mode = new Setting(this, "Mode", "Basic", new ArrayList<>(Arrays.asList("Basic", "Scroll")));

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", true);

	private Setting distance = new Setting(this, "Entity Distance", 7.0D, 6.1D, 15.0D);

	private Setting radius = new Setting(this, "Strafing radius", 3.0D, 0.0D, 6.0D);
	private Setting speed = new Setting(this, "Speed", 0.31D, 0.0D, 1.0D);

	private Setting scrollSpeed = new Setting(this, "Speed to Entity", 0.26D, 0.0D, 1.0D)
			.setVisible(() -> mode.getCurrentMode().equalsIgnoreCase("Scroll"));

	private Setting damageBoost = new Setting(this, "Damaget Boost", false);
	private Setting boost = new Setting(this, "Boost Value", 0.2D, 0.0D, 0.8D).setVisible(() -> damageBoost.isToggle());

	private Entity target;

	private double forward;
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

		if (Helper.getPlayer().distanceTo(target) > radius.getCurrentValueDouble() + 1) {
			forward = 1;
		} else if (Helper.getPlayer().distanceTo(target) < radius.getCurrentValueDouble() + 1
				&& Helper.getPlayer().distanceTo(target) > radius.getCurrentValueDouble() - 0.1) {
			forward = -1;
		} else {
			forward = 0;
		}

		if (Helper.minecraftClient.options.keyLeft.isPressed())
			direction = -1;
		else if (Helper.minecraftClient.options.keyRight.isPressed())
			direction = 1;

		if (Helper.getPlayer().horizontalCollision)
			direction = direction == 1 ? -1 : 1;

		Helper.getPlayer().bodyYaw = getNormalizeYaw(target);
		Helper.getPlayer().headYaw = getNormalizeYaw(target);

		double[] move = move(target);
		MoveUtil.setHVelocity(move[0], move[1]);
	}

	public double[] move(Entity target) {
		double speed = damageBoost.isToggle() && Helper.getPlayer().hurtTime != 0
				? this.speed.getCurrentValueDouble() + boost.getCurrentValueDouble()
				: this.speed.getCurrentValueDouble();

		float yaw = getNormalizeYaw(target);

		double c1 = (Helper.getPlayer().getX() - target.getX())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));
		double s1 = (Helper.getPlayer().getZ() - target.getZ())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));

		double cosX = Math.cos(Math.toRadians(yaw + 90.0F));
		double sinZ = Math.sin(Math.toRadians(yaw + 90.0F));

		double sSpeed = 0;

		if (Helper.getPlayer().distanceTo(target) > radius.getCurrentValueDouble())
			sSpeed = speed;
		else if (Helper.getPlayer().distanceTo(target) < radius.getCurrentValueDouble())
			sSpeed = -speed;

		double x = 0;
		double z = 0;
		if (mode.getCurrentMode().equalsIgnoreCase("Basic")) {
			if (forward > 0) {
				// forwarding to entity (not circle special)
				x = sSpeed * cosX - sSpeed * speed * sinZ;
				z = sSpeed * sinZ - sSpeed * speed * cosX;

			} else if (forward == 0) {
				x = speed * s1 * direction - sSpeed * speed * c1;
				z = -speed * c1 * direction - sSpeed * speed * s1;
			} else if (forward == -1) {
				x = speed * s1 * direction - sSpeed * speed * c1;
				z = -speed * c1 * direction - sSpeed * speed * s1;
			}

		} else if (mode.getCurrentMode().equalsIgnoreCase("Scroll")) {
			x = speed * s1 * direction - scrollSpeed.getCurrentValueDouble() * speed * c1;
			z = -speed * c1 * direction - scrollSpeed.getCurrentValueDouble() * speed * s1;
		}

		return new double[] { x, z };

	}

	public static float getNormalizeYaw(Entity entity) {
		float yaw = RotationUtil.lookAtEntity(entity)[0];
		float f = (float) (Helper.minecraftClient.options.mouseSensitivity * 0.6F + 0.2F);
		float gcd = f * f * f * 1.2F;

		yaw -= yaw % gcd;
		return yaw;
	}

}
