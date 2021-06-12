package org.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.event.MotionEvent;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.MoveUtil;
import org.infinity.utils.entity.EntityUtil;
import org.infinity.utils.rotation.RotationUtil;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@ModuleInfo(category = Category.COMBAT, desc = "Automatically aim at target", key = -2, name = "AimBot", visible = true)
public class AimBot extends Module {

	private Setting look = new Setting(this, "Look", "HEAD", new ArrayList<>(Arrays.asList("HEAD", "BODY", "LEGS")));

	// targets
	private Setting players = new Setting(this, "Players", true);
	private Setting friends = new Setting(this, "Friends", false).setVisible(() -> players.isToggle());
	private Setting invisibles = new Setting(this, "Invisibles", true);
	private Setting mobs = new Setting(this, "Mobs", true);
	private Setting animals = new Setting(this, "Animals", false);

	private Setting throughWalls = new Setting(this, "Through Walls", false);

	// on mooving
	private Setting onMoving = new Setting(this, "On Moving", false);

	private Setting range = new Setting(this, "Range", 3.5D, 0D, 6D);

	private Setting fov = new Setting(this, "FOV", 120D, 0D, 360D);

	private Setting maxSpeed = new Setting(this, "Max Speed %", 60D, 0D, 100D);
	private Setting minSpeed = new Setting(this, "Min Speed %", 20D, 0D, 100D);

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

		event.setRotation(RotationUtil.limitAngleChange(Helper.getPlayer().yaw, look[0], speed),
				RotationUtil.limitAngleChange(Helper.getPlayer().pitch, look[1], speed), true);

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

		return new float[] { i, j };
	}
}
