package me.infinity.features.module.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Aimbot for bow to target", key = -2, name = "BowAim", visible = true)
public class BowAim extends Module {

	private Settings players = new Settings(this, "Players", true, () -> true);
	private Settings invisibles = new Settings(this, "Invisibles", true, () -> true);
	private Settings mobs = new Settings(this, "Mobs", true, () -> true);
	private Settings animals = new Settings(this, "Animals", true, () -> true);
	private Settings range = new Settings(this, "Range", 40, 1, 80, () -> true);
	private Settings speed = new Settings(this, "Speed", 90, 1, 180, () -> true);

	public static Entity target;

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().getMainHandStack().getItem() instanceof BowItem && Helper.getPlayer().isUsingItem()
				&& Helper.getPlayer().getItemUseTime() >= 3) {
			target = EntityUtil.setTarget(range.getCurrentValueInt(), players.isToggle(), invisibles.isToggle(),
					mobs.isToggle(), animals.isToggle());
			if (target == null)
				return;
			double xPos = target.getX();
			double yPos = target.getY();
			double zPos = target.getZ();
			double sideMultiplier = Helper.getPlayer().distanceTo(target)
					/ ((Helper.getPlayer().distanceTo(target) / 2) / 1) * 5;
			double upMultiplier = (Helper.getPlayer().squaredDistanceTo(target) / 320) * 1.1;
			Vec3d vecPos = new Vec3d((xPos - 0.5) + (xPos - target.lastRenderX) * sideMultiplier, yPos + upMultiplier,
					(zPos - 0.5) + (zPos - target.lastRenderZ) * sideMultiplier);
			float[] lookVec = RotationUtils.lookAtVecPos(vecPos, speed.getCurrentValueInt(),
					speed.getCurrentValueInt());
			Helper.getPlayer().yaw = lookVec[0];
			Helper.getPlayer().pitch = lookVec[1];
		}
	}
}
