package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Ingame Infinity Hud", key = GLFW.GLFW_KEY_N, name = "HUD", visible = true)
public class HUD extends Module {

	@Override
	public void onRender(MatrixStack matrices, float tick, int width, int height) {

		List<String> arrayList = new ArrayList<>();

		InfMain.getModuleManager().getList().forEach(module -> {
			if (module.isEnabled() && module.isVisible())
				arrayList.add(module.getSortedName() + " " + Formatting.BLUE + Helper.replaceNull(module.getSuffix()));
		});

		// sort
		arrayList.sort((a, b) -> Integer.compare(FontUtils.getStringWidth(b), FontUtils.getStringWidth(a)));

		float yOffset = 2;
		for (String module : arrayList) {
			float widthOffset = width - FontUtils.getStringWidth(module);
			FontUtils.drawStringWithShadow(matrices, module, widthOffset, yOffset, -1);
			yOffset += 10;
		}
	}	
}
