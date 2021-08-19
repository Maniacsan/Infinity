package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.StringUtil;
import org.infinity.utils.render.Render2D;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Ingame Infinity Hud", key = -2, name = "HUD", visible = true)
public class HUD extends Module {

	private Setting watermark = new Setting(this, "WaterMark", false);
	private Setting markText = new Setting(this, "Mark Text", "Infinity").setVisible(() -> watermark.isToggle());

	private Setting array = new Setting(this, "Arraylist", true);

	private Setting coordinates = new Setting(this, "Coordinates", true);

	private Setting netherCoords = new Setting(this, "Nether Coordinates", false);

	private double animX;

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {
		if (watermark.isToggle())
			markRender(matrices, tick, width, height);
		if (array.isToggle()) {

			List<String> arrayList = new ArrayList<>();

			InfMain.getModuleManager().getList().forEach(module -> {
				if (module.isEnabled() && module.isVisible())
					arrayList.add(
							module.getName() + " " + Formatting.WHITE + StringUtil.replaceNull(module.getSuffix()));
			});

			arrayList.sort((a, b) -> Integer.compare(IFont.legacy15.getWidthIgnoreChar(b),
					IFont.legacy15.getWidthIgnoreChar(a)));

			float yOffset = 1;
			// rainbow
			int count[] = { 0 };

			for (String module : arrayList) {
				float widthOffset = width - IFont.legacy15.getWidthIgnoreChar(module) + 6;

				Render2D.drawRectWH(matrices, widthOffset, yOffset, width, 9, new Color(0, 0, 0, 150).getRGB());
				IFont.legacy15.drawString(matrices, module, widthOffset, yOffset, rainbow(count[0] * 160));

				yOffset += 9;
				count[0]++;
			}
		}
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
			double upY = 23;
			double y2 = Helper.MC.currentScreen instanceof ChatScreen ? upY : 11;

			IFont.legacy17.drawStringWithShadow(matrices, coords, rWidth + 44, height - y2, 0xFFFFFFFF);
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

			double upY = 34;
			double y1 = Helper.MC.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23
					: Helper.MC.currentScreen instanceof ChatScreen && coordinates.isToggle() ? upY
							: coordinates.isToggle() ? 22 : 11;

			IFont.legacy17.drawStringWithShadow(matrices, nCoords, rWidth + 43, height - y1, 0xFFFFFFFF);
		}
	}

	private void markRender(MatrixStack matrices, float tick, int width, int height) {
		IFont.legacy18.drawString(matrices, markText.getText(), 2, 2, 0xFFFFFFFF);
	}

	public static int rainbow(int delay) {
		double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 20.0D);
		rainbow %= 360.0D;
		return Color.getHSBColor((float) -((rainbow / 360.0F)), 0.7F, 0.9F).getRGB();
	}
}