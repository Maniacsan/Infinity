package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Settings;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.MoveUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtils;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Automatically aim at target", key = -2, name = "AimBot", visible = true)
public class AimBot extends Module {

	private Settings look = new Settings(this, "Look", "HEAD", new ArrayList<>(Arrays.asList("HEAD", "BODY", "LEGS")),
			() -> true);

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	private Settings throughWalls = new Settings(this, "Through Walls", false, () -> true);

	// on mooving
	private Settings onMoving = new Settings(this, "On Moving", false, () -> true);

	private Settings range = new Settings(this, "Range", 3.5D, 0D, 6D, () -> true);

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);
	private Settings sens = new Settings(this, "Sensitivity", 45f, 10f, 200f, () -> true);

	private Settings maxSpeed = new Settings(this, "Max Speed %", 60D, 0D, 100D, () -> true);
	private Settings minSpeed = new Settings(this, "Min Speed %", 20D, 0D, 100D, () -> true);

	@EventTarget
	public void onMotionTick(MotionEvent event) {
		Entity target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), fov.getCurrentValueDouble(),
				players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle(),
				throughWalls.isToggle());

		if (target == null)
			return;

		double min = minSpeed.getCurrentValueDouble();
		double max = maxSpeed.getCurrentValueDouble();
		float speed = (float) MathAssist.random(min, max);

		float[] look = look(target);

		if (onMoving.isToggle() && !MoveUtil.isMoving())
			return;

		if (!Float.isNaN(look[0]) || !Float.isNaN(look[1]) || look[1] < 90 || look[1] > -90) {
			Helper.getPlayer().yaw = RotationUtils.limitAngleChange(Helper.getPlayer().yaw, look[0], speed);
			Helper.getPlayer().pitch = RotationUtils.limitAngleChange(Helper.getPlayer().pitch, look[1], speed);
		}

	}

	private float[] look(Entity target) {

		double d = target.getPos().x - Helper.getPlayer().getPos().x;
		double e = target.getPos().z - Helper.getPlayer().getPos().z;
		double pitchPos = 0;

		if (look.getCurrentMode().equalsIgnoreCase("HEAD"))
			pitchPos = 0;
		else if (look.getCurrentMode().equalsIgnoreCase("BODY"))
			pitchPos = 0.5;
		else if (look.getCurrentMode().equalsIgnoreCase("LEGS"))
			pitchPos = 1.4;

		double g;
		if (target instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) target;
			g = livingEntity.getEyeY() - Helper.getPlayer().getEyeY() - pitchPos;
		} else {
			g = (target.getBoundingBox().minY + target.getBoundingBox().maxY) / 2.0D - Helper.getPlayer().getY()
					+ Helper.getPlayer().getEyeY() - pitchPos;
		}

		double h = (double) Math.sqrt(d * d + e * e);
		float i = (float) (Math.atan2(e, d) * 180.0D / Math.PI) - 90.0F;
		float j = (float) (-(Math.atan2(g, h) * 180.0D / Math.PI));

		float pitch = j;
		float yaw = i;

		float m = 0.005f * sens.getCurrentValueFloat();
		float gcd = m * m * m * 1.2f;

		yaw -= yaw % gcd;
		pitch -= pitch % gcd;

		return new float[] { yaw, pitch };
	}
}
