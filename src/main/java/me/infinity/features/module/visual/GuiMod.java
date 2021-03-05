package me.infinity.features.module.visual;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import me.infinity.clickmenu.ClickMenu;
import me.infinity.features.Module;
import me.infinity.features.ModuleInfo;
import me.infinity.features.Settings;
import net.minecraft.client.MinecraftClient;

@ModuleInfo(category = Module.Category.VISUAL, desc = "ImGui based hack gui", key = GLFW.GLFW_KEY_RIGHT_SHIFT, name = "Gui", visible = false)
public class GuiMod extends Module {

	public Settings scale = new Settings(this, "Scale", "60%",
			new ArrayList<>(Arrays.asList(new String[] { "20%", "40%", "60%", "80%", "100%" })), true);

	@Override
	public void onEnable() {
		MinecraftClient.getInstance().openScreen(new ClickMenu());
		super.onEnable();
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
