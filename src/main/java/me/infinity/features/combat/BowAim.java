package me.infinity.features.combat;

import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(category = Module.Category.COMBAT, desc = "Aimbot for bow to target", key = GLFW.GLFW_KEY_R, name = "BowAim", visible = true)
public class BowAim extends Module {

	private Settings players = new Settings(this, "Players", true);
	private Settings invisibles = new Settings(this, "Invisibles", true);
	private Settings mobs = new Settings(this, "Mobs", true);
	private Settings animals = new Settings(this, "Animals", true);

	LivingEntity target;

	@Override
	public void onPlayerTick() {
		if (Helper.getPlayer().getMainHandStack().getItem() instanceof BowItem && Helper.getPlayer().isUsingItem()
				&& Helper.getPlayer().getItemUseTime() >= 3) {
			target = EntityUtil.setTarget(100, players.isToggle(), invisibles.isToggle(), mobs.isToggle(),
					animals.isToggle());
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
			RotationUtils.lookAtVecPos(vecPos, Helper.getPlayer().yaw, Helper.getPlayer().pitch, 360F, 360F);
		}
	}
}
