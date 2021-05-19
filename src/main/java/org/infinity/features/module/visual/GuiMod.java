package org.infinity.features.module.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.infinity.InfMain;
import org.infinity.features.Category;
import org.infinity.features.Module;
import org.infinity.features.ModuleInfo;
import org.infinity.features.Setting;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;

@ModuleInfo(category = Category.VISUAL, desc = "Infinity features menu", key = GLFW.GLFW_KEY_GRAVE_ACCENT, name = "Gui", visible = false)
public class GuiMod extends Module {

	public Setting scale = new Setting(this, "Scale", "60%",
			new ArrayList<>(Arrays.asList(new String[] { "20%", "40%", "60%", "80%", "100%" })));

	public Setting descriptions = new Setting(this, "Description", true);

	public Setting color = new Setting(this, "Color", new Color(49, 109, 214));

	@Override
	public void onEnable() {
		MinecraftClient.getInstance().openScreen(InfMain.INSTANCE.init.menu);
		setEnabled(false);
	}

	public float getScale() {
		float scale1 = 1.0F;
		if (scale.getCurrentMode().equals("100%")) {
			scale1 = 1.8F;
		} else if (scale.getCurrentMode().equals("80%")) {
			scale1 = 1.5F;
		} else if (scale.getCurrentMode().equals("60%")) {
			scale1 = 1.0F;
		} else if (scale.getCurrentMode().equals("40%")) {
			scale1 = 0.7F;
		} else if (scale.getCurrentMode().equals("20%")) {
			scale1 = 0.5F;
		}

		return scale1;
	}

}
