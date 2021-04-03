package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import me.infinity.utils.MathAssist;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Ingame Infinity Hud", key = GLFW.GLFW_KEY_N, name = "HUD", visible = true)
public class HUD extends Module {

	private Settings array = new Settings(this, "Arraylist", true, () -> true);

	private Settings coordinates = new Settings(this, "Coordinates", true, () -> true);

	private Settings netherCoords = new Settings(this, "Nether Coordinates", false, () -> true);

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {

		List<String> arrayList = new ArrayList<>();

		InfMain.getModuleManager().getList().forEach(module -> {
			if (module.isEnabled() && module.isVisible())
				arrayList.add(module.getSortedName() + " " + Formatting.BLUE + Helper.replaceNull(module.getSuffix()));
		});

		// sort
		arrayList.sort((a, b) -> Integer.compare(FontUtils.getStringWidth(b), FontUtils.getStringWidth(a)));

		float yOffset = 1;
		if (array.isToggle()) {
			for (String module : arrayList) {
				float widthOffset = width - FontUtils.getStringWidth(module);
				FontUtils.drawStringWithShadow(matrices, module, widthOffset + 1, yOffset, -1);
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

			double y1 = Helper.minecraftClient.currentScreen instanceof ChatScreen && !coordinates.isToggle() ? 23 : Helper.minecraftClient.currentScreen instanceof ChatScreen && coordinates.isToggle() ? 34 : coordinates.isToggle() ? 22 : 11;

			FontUtils.drawStringWithShadow(matrices, nCoords, rWidth - 2, height - y1, 0xFFFFFFFF);
		}

	}
}
