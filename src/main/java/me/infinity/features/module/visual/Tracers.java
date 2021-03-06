package me.infinity.features.module.visual;

import org.lwjgl.opengl.GL11;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
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

		if (entity == null)
			return;

		double x1 = entity.getX() - Helper.getPlayer().getX();
		double y1 = entity.getY() - Helper.getPlayer().getY();
		double z1 = entity.getZ() - Helper.getPlayer().getZ();
		
		double h = (double) MathHelper.sqrt(x1 * x1 + z1 * z1);
		float yaw = (float) -(Math.atan2(x1, z1) * 57.29577951308232) - Helper.getPlayer().yaw;
		float pitch = (float) (-(MathHelper.atan2(y1, h) * 57.2957763671875D) - Helper.getPlayer().pitch);
		yaw = (float) Math.toRadians(yaw);
		float rotate = yaw;
		FontUtils.drawStringWithShadow(matrices, "rotate - " + rotate, 2, 20, -1);
		int k = -1;
		double c = Math.sin(yaw) * (radius.getCurrentValueDouble() * 40);
		double s = Math.cos(pitch) * Math.sin(Math.toRadians(MathHelper.clamp(90, -90.0f, 90.0f)))
				* (radius.getCurrentValueDouble() * 40);
		double x2 = (width / 2 - 2) + c;
		double y2 = (height / 2 - 5) + s;
		double x = (width / 2 - 2);
		double y = (height / 2 - 5);
		int size = 5;
		float f = (k >> 24 & 0xFF) / 255.0F;
		float f1 = (k >> 16 & 0xFF) / 255.0F;
		float f2 = (k >> 8 & 0xFF) / 255.0F;
		float f3 = (k & 0xFF) / 255.0F;
		GL11.glPushMatrix();
		GL11.glTranslated(x2, y2, 0);
		GL11.glRotatef((float) yaw, 0.f, 0.f, size / 2.f);
		GL11.glColor4f(f1, f2, f3, f);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glBegin(4);
		GL11.glVertex2d(0, 0);
		GL11.glVertex2d((-size), (-size));
		GL11.glVertex2d((-size), (size));
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glTranslated(0, 0, -15);
		GL11.glPopMatrix();

	}

	private double getX() {
		double x = 0;
		for (int i = 0; i <= 360; i++) {
			x = Math.sin(i * 3.141526D / 180.0D) * radius.getCurrentValueDouble();
		}
		return x;
	}

	private double getY() {
		double y = 0;
		for (int i = 0; i <= 360; i++) {
			y = Math.cos(i * 3.141526D / 180.0D) * Math.sin(Math.toRadians(MathHelper.clamp(90.0f, -90.0f, 90.0f)))
					* (radius.getCurrentValueDouble() * 40);
		}
		return y;

	}

}
