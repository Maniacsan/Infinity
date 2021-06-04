package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Ingame Infinity Hud", key = -2, name = "HUD", visible = true)
public class HUD extends Module {

	private Setting array = new Setting(this, "Arraylist", true);
	private Setting arrayColor = new Setting(this, "List Color", new Color(50, 109, 220));

	private Setting coordinates = new Setting(this, "Coordinates", true);

	private Setting netherCoords = new Setting(this, "Nether Coordinates", false);

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {

		List<String> arrayList = new ArrayList<>();

		InfMain.getModuleManager().getList().forEach(module -> {
			if (module.isEnabled() && module.isVisible())
				arrayList.add(Formatting.WHITE + module.getName() + " " + Formatting.RESET
						+ StringUtil.replaceNull(module.getSuffix()));
		});

		arrayList.sort(
				(a, b) -> Integer.compare(IFont.legacy17.getStringWidth(b), IFont.legacy17.getStringWidth(a)));

		if (Helper.minecraftClient.options.debugEnabled)
			return;

		float yOffset = 1;
		if (array.isToggle()) {
			for (String module : arrayList) {
				float widthOffset = width - IFont.legacy17.getWidthIgnoreChar(module) + 14;
				Render2D.drawRectWH(matrices, widthOffset, yOffset, widthOffset - IFont.legacy17.getStringWidth(module),
						10, 0x90000000);
				IFont.legacy17.drawString(module, widthOffset, yOffset, arrayColor.getColor().getRGB());
				yOffset += 10;
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
			double y2 = Helper.minecraftClient.currentScreen instanceof ChatScreen ? 23 : 11;
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

			double y1 = Helper.minecraftClient.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23
					: Helper.minecraftClient.currentScreen instanceof ChatScreen && coordinates.isToggle() ? 34
							: coordinates.isToggle() ? 22 : 11;

			IFont.legacy17.drawStringWithShadow(nCoords, rWidth + 43, height - y1, 0xFFFFFFFF);
		}
	}
}
