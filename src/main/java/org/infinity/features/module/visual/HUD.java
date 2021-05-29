package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.MathAssist;
import org.infinity.utils.StringUtil;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Category.VISUAL, desc = "Ingame Infinity Hud", key = -2, name = "HUD", visible = true)
public class HUD extends Module {

	private Setting array = new Setting(this, "Arraylist", true);
	private Setting arrayColor = new Setting(this, "List Color", new Color(50, 109, 220));

	private Setting coordinates = new Setting(this, "Coordinates", true);

	private Setting netherCoords = new Setting(this, "Nether Coordinates", false);
	
	private double animation;

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {

		List<String> arrayList = new ArrayList<>();

		InfMain.getModuleManager().getList().forEach(module -> {
			if (module.isEnabled() && module.isVisible())
				arrayList.add(Formatting.WHITE + module.getSortedName() + " " + Formatting.RESET
						+ StringUtil.replaceNull(module.getSuffix()));
		});

		// sort
		arrayList.sort((a, b) -> Integer.compare(FontUtils.getStringWidth(b), FontUtils.getStringWidth(a)));

		if (Helper.minecraftClient.options.debugEnabled)
			return;

		float yOffset = 1;
		if (array.isToggle()) {
			for (String module : arrayList) {
				float widthOffset = width - FontUtils.getStringWidth(module);
				animation = (int) RenderUtil.animate(widthOffset, animation, 1);
				FontUtils.drawStringWithShadow(matrices, module, animation , yOffset,
						arrayColor.getColor().getRGB());
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
			double rWidth = width - FontUtils.getStringWidth(coords);
			double y2 = Helper.minecraftClient.currentScreen instanceof ChatScreen ? 23 : 11;
			FontUtils.drawStringWithShadow(matrices, coords, rWidth - 2, height - y2, 0xFFFFFFFF);
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
			double rWidth = width - FontUtils.getStringWidth(nCoords);

			double y1 = Helper.minecraftClient.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23
					: Helper.minecraftClient.currentScreen instanceof ChatScreen && coordinates.isToggle() ? 34
							: coordinates.isToggle() ? 22 : 11;

			FontUtils.drawStringWithShadow(matrices, nCoords, rWidth - 2, height - y1, 0xFFFFFFFF);
		}

	}
}
