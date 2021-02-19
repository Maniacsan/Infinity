package me.infinity.features.module.visual;

import java.util.Comparator;

import org.lwjgl.glfw.GLFW;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.client.util.math.MatrixStack;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Ingame Infinity Hud", key = GLFW.GLFW_KEY_N, name = "HUD", visible = true)
public class HUD extends Module {

	@Override
	public void onRender(MatrixStack matrices, int width, int height) {
		Helper.getRenderUtil().draw("Infinity", 2, 2, -1);

		// sort
		InfMain.getModuleManager().getList().sort(new Comparator<Module>() {
			@Override
			public int compare(Module m, Module m2) {
				return ((Integer) FontUtils.getStringWidth(m2.toCompare()))
						.compareTo(FontUtils.getStringWidth(m.toCompare()));
			}
		});

		float yOffset = 2;
		for (Module module : InfMain.getModuleManager().getList()) {
			if (module.isEnabled() && module.isVisible()) {
				float widthOffset = width - FontUtils
						.getStringWidth(module.getSortedName() + " " + Helper.replaceNull(module.getSuffix()));
				FontUtils.drawStringWithShadow(matrices,
						module.getSortedName() + " " + Helper.replaceNull(module.getSuffix()), widthOffset, yOffset,
						-1);
				yOffset += 10;
			}
		}
	}
}
