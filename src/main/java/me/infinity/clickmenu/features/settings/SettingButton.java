package me.infinity.clickmenu.features.settings;

import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class SettingButton {

	public Settings setting;
	public double height;

	public SettingButton(Settings setting) {
		this.setting = setting;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.height = height;
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
	}

	public void mouseScrolled(double d, double e, double amount) {
	}

	public boolean isVisible() {
		return true;
	}
}
