package me.infinity.features.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.infinity.InfMain;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.utils.Helper;
import net.minecraft.client.font.TextRenderer;

@ModuleInfo(category = Module.Category.VISUAL, desc = "Ingame Infinity Hud", key = GLFW.GLFW_KEY_N, name = "HUD", visible = true)
public class HUD extends Module {
	
	@Override
	public void onRender(int width, int height) {
		TextRenderer textRenderer = Helper.minecraftClient.textRenderer;
		Helper.getRenderUtil().draw("Infinity", 2, 2, -1);
		
		List<Module> arrList = new ArrayList<>();
		Collections.sort(arrList, Comparator.comparing(Module::getName));

		float yOffset = 2;
		for (Module module : InfMain.getModuleManager().getList()) {
			if (module.isEnabled()) {
				float widthOffset = width - textRenderer.getWidth(module.getName());
				Helper.getRenderUtil().draw(module.getName(), widthOffset, yOffset, -1);
				yOffset += 10;
			}
		}
	}
}
