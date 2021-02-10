package me.infinity.clickmenu.features.settings;

import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class SettingButton {

	public Settings setting;
	protected double x;
	protected double y;
	protected double width;
	protected double height;

	public SettingButton(Settings setting) {
		this.setting = setting;
	}
	
	public SettingButton(Settings setting, double x, double y, double width, double height) {
		this.setting = setting;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
	}
	
	public void mouseReleased(double mouseX, double mouseY, int button) {
	}
}
