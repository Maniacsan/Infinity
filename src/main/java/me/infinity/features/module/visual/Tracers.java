package me.infinity.features.module.visual;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.EntityUtil;
import me.infinity.utils.Helper;
import me.infinity.utils.RotationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@ModuleInfo(category = Module.Category.VISUAL, desc = "", key = -2, name = "Tracers", visible = true)
public class Tracers extends Module {

	private Settings players = new Settings(this, "Players", true, true);
	private Settings invisibles = new Settings(this, "Invisibles", true, true);
	private Settings mobs = new Settings(this, "Mobs", true, true);
	private Settings animals = new Settings(this, "Animals", true, true);
	private Settings radius = new Settings(this, "Radius", 2D, 1D, 8D, true);

	@Override
	public void onRender(MatrixStack matrices, float tickDelta, int width, int height) {
		Entity entity = EntityUtil.setRenderTarget(players.isToggle(), invisibles.isToggle(), mobs.isToggle(),
				animals.isToggle());
		double d = entity.getX() - Helper.getPlayer().getX();
		double e = entity.getZ() - Helper.getPlayer().getZ();
        
		float i = (float) (MathHelper.atan2(e, d) * 57.2957763671875D) + 90F;
		double rot = RotationUtils.getYaw(entity);
		rot = Math.toRadians(rot);
		double pos = getRotation(rot);
		FontUtils.drawStringWithShadow(matrices, "W", (width / 2 - 2) + getX(pos), (height / 2 - 5) + getY(pos), -1);

	}

	private double getX(double rad) {
		return Math.sin(rad) * (radius.getCurrentValueDouble() * 40);
	}

	private double getY(double rad) {
		return Math.cos(rad) * Math.sin(Math.toRadians(MathHelper.clamp(90.0f, -90.0f, 90.0f)))
				* (radius.getCurrentValueDouble() * 40);
	}

	private double getRotation(double rotation) {
		double yaw = 0;
		yaw = Helper.getPlayer().yaw;

		return Math.toRadians(MathHelper.wrapDegrees(yaw)) - rotation;
	}

}
