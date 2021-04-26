package org.infinity.features.module.movement;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.PlayerMoveEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.entity.EntityUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.MOVEMENT, desc = "Whirls in a circle of entity", key = -2, name = "TargetStrafe", visible = true)
public class TargetStrafe extends Module {

	private Settings mode = new Settings(this, "Mode", "Basic", new ArrayList<>(Arrays.asList("Basic", "Scroll")),
			() -> true);

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);

	private Settings distance = new Settings(this, "Entity Distance", 7.0D, 6.1D, 15.0D, () -> true);

	private Settings radius = new Settings(this, "Strafing radius", 3.0D, 0.0D, 6.0D, () -> true);
	private Settings speed = new Settings(this, "Speed", 0.31D, 0.0D, 1.0D, () -> true);

	private Settings scrollSpeed = new Settings(this, "Speed to Entity", 0.26D, 0.0D, 1.0D,
			() -> mode.getCurrentMode().equalsIgnoreCase("Scroll"));

	private Settings damageBoost = new Settings(this, "Damaget Boost", false, () -> true);
	private Settings boost = new Settings(this, "Boost Value", 0.2D, 0.0D, 0.8D, () -> damageBoost.isToggle());

	private Entity target;

	private int direction = 1;

	@Override
	public void onPlayerTick() {
		setSuffix(mode.getCurrentMode());

		target = EntityUtil.setTarget(distance.getCurrentValueDouble(), 360, players.isToggle(), friends.isToggle(),
				invisibles.isToggle(), mobs.isToggle(), animals.isToggle(), true);

		if (target == null)
			return;

		if (Helper.getPlayer().isOnGround())
			Helper.getPlayer().jump();

		if (Helper.minecraftClient.options.keyLeft.isPressed())
			direction = -1;
		else if (Helper.minecraftClient.options.keyRight.isPressed())
			direction = 1;

		if (Helper.getPlayer().horizontalCollision)
			direction = direction == 1 ? -1 : 1;
	}

	@EventTarget
	public void onMove(PlayerMoveEvent event) {
		if (target == null)
			return;

		double speed = damageBoost.isToggle() && Helper.getPlayer().hurtTime != 0
				? this.speed.getCurrentValueDouble() + boost.getCurrentValueDouble()
				: this.speed.getCurrentValueDouble();

		double strafeYaw = Math.atan2(target.getZ() - Helper.getPlayer().getZ(),
				target.getX() - Helper.getPlayer().getX());
		double distance = Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
				+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2));

		double yaw = strafeYaw - (0.5 * Math.PI);
		float f = (float) (Helper.minecraftClient.options.mouseSensitivity * 0.6F + 0.2F);
		float gcd = f * f * f * 1.2F;

		yaw -= yaw % gcd;

		// set radius
		double circle = distance - radius.getCurrentValueDouble() < -speed ? -speed
				: distance - radius.getCurrentValueDouble();

		// speed borders
		if (circle > speed)
			circle = speed;
		else if (circle < -speed)
			circle = speed;

		// creating circle
		double circleSin = -Math.sin(yaw) * circle;
		double circleCos = Math.cos(yaw) * circle;

		double c1 = (Helper.getPlayer().getX() - target.getX())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));
		double s1 = (Helper.getPlayer().getZ() - target.getZ())
				/ (Math.sqrt(Math.pow(Helper.getPlayer().getX() - target.getX(), 2)
						+ Math.pow(Helper.getPlayer().getZ() - target.getZ(), 2)));

		double x = speed * s1 * direction + circleSin * speed;
		double z = -speed * c1 * direction + circleCos * speed;

		if (Helper.getPlayer().distanceTo(target) < radius.getCurrentValueDouble() + 0.09 &&
				Helper.getPlayer().distanceTo(target) > radius.getCurrentValueDouble() - 0.09) {
			x = circleSin + -Math.sin(strafeYaw) * speed * direction;
			z = circleCos + Math.cos(strafeYaw) * speed * direction;
		}

		double scrollX = speed * s1 * direction - scrollSpeed.getCurrentValueDouble() * speed * c1;
		double scrollZ = -speed * c1 * direction - scrollSpeed.getCurrentValueDouble() * speed * s1;

		if (mode.getCurrentMode().equalsIgnoreCase("Basic")) {
			event.setVec3d(new Vec3d(x, Helper.getPlayer().getVelocity().getY(), z));
		} else if (mode.getCurrentMode().equalsIgnoreCase("Scroll")) {
			event.setVec3d(new Vec3d(scrollX, Helper.getPlayer().getVelocity().getY(), scrollZ));
		}

	}

}