package me.infinity.features.module.combat;

import java.util.ArrayList;
import java.util.Arrays;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MathAssist;
import me.infinity.utils.MoveUtil;
import me.infinity.utils.entity.EntityUtil;
import me.infinity.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Help aiming", key = -2, name = "AimAssist", visible = true)
public class AimAssist extends Module {

	private Settings look = new Settings(this, "Look", "HEAD", new ArrayList<>(Arrays.asList("HEAD", "BODY", "LEGS")),
			() -> true);

	// targets
	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings friends = new Settings(this, "Friends", false, () -> players.isToggle());
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", false, () -> true);

	// on mooving
	private Settings onMoving = new Settings(this, "On Moving", false, () -> true);

	private Settings range = new Settings(this, "Range", 3.5D, 0D, 6D, () -> true);

	private Settings fov = new Settings(this, "FOV", 120D, 0D, 360D, () -> true);
	private Settings sens = new Settings(this, "Sensitivity", 45f, 10f, 200f, () -> true);

	private Settings maxSpeed = new Settings(this, "Max Speed %", 60D, 0D, 100D, () -> true);
	private Settings minSpeed = new Settings(this, "Min Speed %", 20D, 0D, 100D, () -> true);

	@Override
	public void onPlayerTick() {
		Entity target = EntityUtil.setTarget(this.range.getCurrentValueDouble(), fov.getCurrentValueDouble(),
				players.isToggle(), friends.isToggle(), invisibles.isToggle(), mobs.isToggle(), animals.isToggle());

		if (target == null)
			return;

		float[] look = look(target);

		if (onMoving.isToggle() && !MoveUtil.isMoving())
			return;

		Helper.getPlayer().yaw = look[0];
		Helper.getPlayer().pitch = look[1];

	}

	private float[] look(Entity target) {
		double min = minSpeed.getCurrentValueDouble() / 5;
		double max = maxSpeed.getCurrentValueDouble() / 5;
		double speed = MathAssist.random(min, max);

		double d = target.getX() - Helper.getPlayer().getX();
		double e = target.getZ() - Helper.getPlayer().getZ();
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

		double h = (double) MathHelper.sqrt(d * d + e * e);
		float i = (float) (MathHelper.atan2(e, d) * 57.2957763671875D) - 90.0F;
		float j = (float) (-(MathHelper.atan2(g, h) * 57.2957763671875D));
		float yaw = Helper.getPlayer().yaw;
		float pitch = Helper.getPlayer().pitch;
		pitch = RotationUtils.limitAngleChange(pitch, j, (float) speed);
		yaw = RotationUtils.limitAngleChange(yaw, i, (float) speed);
		float m = 0.005f * sens.getCurrentValueFloat();
		float gcd = m * m * m * 1.2f;

		yaw -= yaw % gcd;
		pitch -= pitch % gcd;
		return new float[] { yaw, pitch };
	}
}
