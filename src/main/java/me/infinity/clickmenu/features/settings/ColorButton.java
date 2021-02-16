package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ColorButton extends SettingButton {

	private boolean extended;

	private boolean selectingHue;

	private boolean selectingSB;

	private boolean hovered;
	
	private boolean hoveredExtended;

	private boolean hueHovered;

	public ColorButton(Settings setting) {
		super(setting);
		this.selectingHue = false;
		this.selectingSB = false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		Render2D.drawGradientRect(matrices, x, y, width, height, Color.RED.getRGB(), Color.white.getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (!this.extended) {
				if (hoveredExtended) {
					this.extended = true;
				}
			} else {
				if (hovered) {
					this.selectingSB = true;
				}
				if (hueHovered) {
					this.selectingHue = true;
				}
			}
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.selectingHue = false;
		this.selectingSB = false;
	}

}
