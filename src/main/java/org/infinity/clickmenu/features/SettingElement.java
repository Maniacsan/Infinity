package org.infinity.clickmenu.features;

import org.infinity.features.Setting;

import net.minecraft.client.util.math.MatrixStack;

public abstract class SettingElement {

	public Setting setting;
	public double height;

	public SettingElement(Setting setting) {
		this.setting = setting;
	}

	public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y,
			double width, double height);

	public abstract void mouseClicked(double mouseX, double mouseY, int button);

	public abstract void mouseReleased(double mouseX, double mouseY, int button);

	public abstract void mouseScrolled(double d, double e, double amount);

	public abstract void onClose();

	public abstract boolean isVisible();
}
