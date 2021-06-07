package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.StringUtil;
import org.infinity.utils.entity.EntityUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Ingame Infinity Hud", key = -2, name = "HUD", visible = true)
public class HUD extends Module {

	public Setting scale = new Setting(this, "Scale", "60%",
			new ArrayList<>(Arrays.asList(new String[] { "40%", "60%", "80%", "100%" })));
	private Setting array = new Setting(this, "Arraylist", true);

	private Setting coordinates = new Setting(this, "Coordinates", true);

	private Setting netherCoords = new Setting(this, "Nether Coordinates", false);

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {
		if (Helper.minecraftClient.options.debugEnabled)
			return;

		double scale = getScale();

		GL11.glPushMatrix();
		GL11.glTranslated(width, 0, 0);
		GL11.glScaled(scale, scale, scale);
		GL11.glTranslated(-width, 0, 0);

		if (array.isToggle()) {

			List<String> arrayList = new ArrayList<>();

			InfMain.getModuleManager().getList().forEach(module -> {
				if (module.isEnabled() && module.isVisible())
					arrayList.add(Formatting.WHITE + module.getName() + " " + Formatting.RESET
							+ StringUtil.replaceNull(module.getSuffix()));
			});

			arrayList.sort((a, b) -> Integer.compare(IFont.legacy15.getWidthIgnoreChar(b),
					IFont.legacy15.getWidthIgnoreChar(a)));

			float yOffset = 1;
			// rainbow
			int count[] = { 0 };

			for (String module : arrayList) {
				float widthOffset = width - IFont.legacy15.getWidthIgnoreChar(module) + 13;

				Render2D.fillSideGradient(width, yOffset, width - IFont.legacy15.getWidthIgnoreChar(module) + 10,
						yOffset + 9, new Color(0, 0, 0, 150).getRGB(), 0x10000000);
				IFont.legacy15.drawString(module, widthOffset, yOffset, rainbow(count[0] * 160));

				yOffset += 9;
				count[0]++;
			}
		}
		GL11.glPopMatrix();

		GL11.glPushMatrix();

		GL11.glTranslated(width, height, 0);
		GL11.glScaled(scale, scale, scale);
		GL11.glTranslated(-width, -height, 0);

		if (coordinates.isToggle()) {
			double x = Helper.getPlayer().getX();
			double y = Helper.getPlayer().getY();
			double z = Helper.getPlayer().getZ();

			x = MathAssist.round(x, 1);
			y = MathAssist.round(y, 1);
			z = MathAssist.round(z, 1);

			String coords = Formatting.BLUE + "x" + Formatting.WHITE + ": " + x + Formatting.BLUE + " y"
					+ Formatting.WHITE + ": " + y + Formatting.BLUE + " z" + Formatting.WHITE + ": " + z;
			double rWidth = width - IFont.legacy17.getWidthIgnoreChar(coords);
			double upY = this.scale.getCurrentMode().equalsIgnoreCase("100%") ? 18
					: this.scale.getCurrentMode().equalsIgnoreCase("80%") ? 19
							: this.scale.getCurrentMode().equalsIgnoreCase("40%") ? 29 : 23;
			double y2 = Helper.minecraftClient.currentScreen instanceof ChatScreen ? upY : 11;

			IFont.legacy17.drawStringWithShadow(coords, rWidth + 44, height - y2, 0xFFFFFFFF);
		}

		if (netherCoords.isToggle()) {
			double x = Helper.getPlayer().getX() / 8;
			double y = Helper.getPlayer().getY() / 8;
			double z = Helper.getPlayer().getZ() / 8;

			x = MathAssist.round(x, 1);
			y = MathAssist.round(y, 1);
			z = MathAssist.round(z, 1);

			String nCoords = Formatting.RED + "x" + Formatting.WHITE + ": " + x + Formatting.RED + " y"
					+ Formatting.WHITE + ": " + y + Formatting.RED + " z" + Formatting.WHITE + ": " + z;
			double rWidth = width - IFont.legacy17.getWidthIgnoreChar(nCoords);

			double upY = this.scale.getCurrentMode().equalsIgnoreCase("100%") ? 29
					: this.scale.getCurrentMode().equalsIgnoreCase("80%") ? 30
							: this.scale.getCurrentMode().equalsIgnoreCase("40%") ? 40 : 34;
			double y1 = Helper.minecraftClient.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23
					: Helper.minecraftClient.currentScreen instanceof ChatScreen && coordinates.isToggle() ? upY
							: coordinates.isToggle() ? 22 : 11;

			IFont.legacy17.drawStringWithShadow(nCoords, rWidth + 43, height - y1, 0xFFFFFFFF);
		}

		GL11.glPopMatrix();
	}

	public double getScale() {
		double scale1 = 1.0F;
		switch (scale.getCurrentMode()) {
		case "100%":
			scale1 = 1.5;
			break;
		case "80%":
			scale1 = 1.2;
			break;
		case "60%":
			scale1 = 1.0;
			break;
		case "40%":
			scale1 = 0.7;
			break;
		}
		return scale1;
	}

	public static int rainbow(int delay) {
		double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 20.0D);
		rainbow %= 360.0D;
		return Color.getHSBColor((float) -((rainbow / 360.0F)), 0.7F, 0.7F).getRGB();
	}
}
