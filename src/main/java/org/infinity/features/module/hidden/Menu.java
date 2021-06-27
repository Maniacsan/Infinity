package org.infinity.features.module.hidden;

import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.main.InfMain;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;

@ModuleInfo(category = Category.HIDDEN, desc = "Infinity features menu", key = GLFW.GLFW_KEY_GRAVE_ACCENT, name = "Menu", visible = false)
public class Menu extends Module {

	@Override
	public void onEnable() {
		MinecraftClient.getInstance().openScreen(InfMain.INSTANCE.init.menu);
		setEnabled(false);
	}
}
