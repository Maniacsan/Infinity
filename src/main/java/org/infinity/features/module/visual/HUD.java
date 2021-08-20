package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.render.Render2D;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Ingame Infinity Hud", key = -2, name = "HUD", visible = true)
public class HUD extends Module {

	public Setting watermark = new Setting(this, "WaterMark", false);

	// Array List
	private Setting array = new Setting(this, "Arraylist", true);
	private Setting listPosition = new Setting(this, "List Position", "Below",
			new ArrayList<>(Arrays.asList("Top", "Below"))).setVisible(() -> array.isToggle());
	private Setting transparencyBG = new Setting(this, "Transparency", 200D, 0D, 255D)
			.setVisible(() -> array.isToggle());
	private Setting animSpeed = new Setting(this, "Anim Speed", 0.7D, 0.1D, 0.95D).setVisible(() -> array.isToggle());
	private Setting offsetY = new Setting(this, "Offset Y", 1.7D, 0D, 5D).setVisible(() -> array.isToggle());
	private Setting colorMode = new Setting(this, "Color Mode", "Rainbow",
			new ArrayList<>(Arrays.asList("Rainbow", "Pulse", "Custom"))).setVisible(() -> array.isToggle());

	private Setting rainbowGradient = new Setting(this, "Gradient", 140D, 0D, 300D)
			.setVisible(() -> array.isToggle() && colorMode.getCurrentMode().equalsIgnoreCase("Rainbow"));
	private Setting rainbowDelay = new Setting(this, "Rainbow Delay", 14D, 1D, 30D)
			.setVisible(() -> array.isToggle() && colorMode.getCurrentMode().equalsIgnoreCase("Rainbow"));

	private Setting arrayColor = new Setting(this, "Array Color", new Color(250, 250, 250))
			.setVisible(() -> array.isToggle() && colorMode.getCurrentMode().equalsIgnoreCase("Custom")
					|| colorMode.getCurrentMode().equalsIgnoreCase("Pulse"));

	private Setting coordinates = new Setting(this, "Coordinates", true);

	private Setting netherCoords = new Setting(this, "Nether Coordinates", false);

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {
		if (watermark.isToggle())
			markRender(matrices, tick, width, height);

		if (array.isToggle())
			listRender(matrices, tick, width, height);

		boolean belowPosition = array.isToggle() && listPosition.getCurrentMode().equalsIgnoreCase("Below");

		double playerX = MathAssist.round(Helper.getPlayer().getX(), 1);
		double playerY = MathAssist.round(Helper.getPlayer().getY(), 1);
		double playerZ = MathAssist.round(Helper.getPlayer().getZ(), 1);

		if (coordinates.isToggle()) {

			String coords = Formatting.BLUE + "x" + Formatting.WHITE + ": " + playerX + Formatting.BLUE + " y"
					+ Formatting.WHITE + ": " + playerY + Formatting.BLUE + " z" + Formatting.WHITE + ": " + playerZ;

			double countY = Helper.MC.currentScreen instanceof ChatScreen ? 23 : 11;
			double posY = belowPosition ? 1 : height - countY;
			double posX = width - IFont.legacy17.getWidthIgnoreChar(coords);

			IFont.legacy17.drawStringWithShadow(matrices, coords, posX + 44, posY, 0xFFFFFFFF);
		}

		if (netherCoords.isToggle()) {
			playerX = MathAssist.round(playerX / 8, 1);
			playerY = MathAssist.round(playerY / 8, 1);
			playerZ = MathAssist.round(playerZ / 8, 1);

			String netherCoords = Formatting.RED + "x" + Formatting.WHITE + ": " + playerX + Formatting.RED + " y"
					+ Formatting.WHITE + ": " + playerY + Formatting.RED + " z" + Formatting.WHITE + ": " + playerZ;
			double countY = Helper.MC.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23
					: Helper.MC.currentScreen instanceof ChatScreen && coordinates.isToggle() ? 34
							: coordinates.isToggle() ? 22 : 11;
			double posY = belowPosition && coordinates.isToggle() ? 11 : belowPosition ? 1 : height - countY;
			double posX = width - IFont.legacy17.getWidthIgnoreChar(netherCoords);

			IFont.legacy17.drawStringWithShadow(matrices, netherCoords, posX + 43, posY, 0xFFFFFFFF);
		}
	}

	private void listRender(MatrixStack matrices, float tick, int width, int height) {
		List<Module> arrayList = new ArrayList<>();

		InfMain.getModuleManager().getList().forEach(module -> {
			arrayList.add(module);
		});

		arrayList.sort((a, b) -> Integer.compare(IFont.legacy15.getWidthIgnoreChar(b.getRenderName()),
				IFont.legacy15.getWidthIgnoreChar(a.getRenderName())));

		float offsetY = 0;
		int counter[] = { 0 };

		double posY = listPosition.getCurrentMode().equalsIgnoreCase("Top") ? 2
				: (listPosition.getCurrentMode().equalsIgnoreCase("Below"))
						? height - IFont.legacy15.getFontHeight() - 2
						: 2;

		int color = -1;

		for (Module module : arrayList) {
			boolean enabled = module.isEnabled() && module.isVisible();

			switch (colorMode.getCurrentMode()) {
			case "Rainbow":
				color = rainbow((int) (counter[0] * rainbowGradient.getCurrentValueDouble()),
						rainbowDelay.getCurrentValueDouble());
				break;
			case "Pulse":
				color = fade(arrayColor.getColor(), 200, 70);
				break;
			case "Custom":
				color = arrayColor.getColor().getRGB();
				break;
			}

			module.animY = (float) (enabled ? Math.min(9 + this.offsetY.getCurrentValueDouble(), module.animY + 5)
					: module.animX > width - (IFont.legacy15.getWidthIgnoreChar(module.getRenderName()) / 2 + 6) ? Math.max(0, module.animY - 5)
							: 9 + this.offsetY.getCurrentValueDouble());
			float posX = width - IFont.legacy15.getWidthIgnoreChar(module.getRenderName()) + 6;
			module.animX = (float) (enabled && module.animY > 8
					? RenderUtil.animate(posX, module.animX, animSpeed.getCurrentValueDouble())
					: RenderUtil.animate(width + 2, module.animX, animSpeed.getCurrentValueDouble()));

			if (module.animX < width - 1) {
				Render2D.drawRectWH(matrices, module.animX - 1, posY + offsetY, width,
						9 + this.offsetY.getCurrentValueDouble(),
						new Color(0, 0, 0, (int) transparencyBG.getCurrentValueDouble()).getRGB());
				IFont.legacy15.drawString(matrices, module.getRenderName(), module.animX,
						posY + offsetY + (this.offsetY.getCurrentValueDouble() / 2), color);

				counter[0]++;
			}

			if (listPosition.getCurrentMode().equalsIgnoreCase("Top"))
				offsetY += module.animY;
			else if (listPosition.getCurrentMode().equalsIgnoreCase("Below"))
				offsetY -= module.animY;
		}
	}

	private void markRender(MatrixStack matrices, float tick, int width, int height) {
		IFont.legacy18.drawString(matrices, "INFINITY", 2, 2, 0xFFFFFFFF);
	}

	private int rainbow(int delay, double speed) {
		double rainbow = Math.ceil((System.currentTimeMillis() + delay) / speed);
		rainbow %= 360.0D;
		return Color.getHSBColor((float) -((rainbow / 360.0F)), 0.7F, 0.8F).getRGB();
	}

	private int fade(Color color, int index, int count) {
		float[] hsb = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		float brightness = Math
				.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + index / count * 2.0F) % 2.0F - 1.0F);
		brightness = 0.5F + 0.5F * brightness;
		hsb[2] = brightness % 2.0F;
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}
}