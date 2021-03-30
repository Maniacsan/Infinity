package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ColorButton extends SettingButton {

	private boolean extended;
	private float hue, saturation, brightness;
	private boolean pressedhue;
	private boolean pressedSB;
	private boolean extendedHovered;
	private boolean hovered;
	private boolean hueHovered;
	private boolean noExtHover;
	private double posX, posY;

	public ColorButton(Settings setting) {
		super(setting);
		float[] hsb = new float[3];
		Color clr = setting.getColor();
		hsb = Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
		this.hue = hsb[0];
		this.saturation = hsb[1];
		this.brightness = hsb[2];
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x1, double y1, double width,
			double height) {
		double hueHeight = 60;
		double sbWidth = 70;
		double x = x1 + 91;
		double y = y1 + 3;
		noExtHover = Render2D.isHovered(mouseX, mouseY, x, y, sbWidth + 12, hueHeight + 2);
		extendedHovered = Render2D.isHovered(mouseX, mouseY, x1, y1, width - 10, height);
		hovered = Render2D.isHovered(mouseX, mouseY, x, y, sbWidth - 1, hueHeight);
		hueHovered = Render2D.isHovered(mouseX, mouseY, x + 73, y, 7, hueHeight);
		FontUtils.drawStringWithShadow(matrices, setting.getName(), x1, y1 + 2, -1);
		Render2D.drawRectWH(matrices, x1 + 75.5, y1 + 3, extended ? 14 : 13, 7, 0xFF161616);
		Render2D.drawRectWH(matrices, x1 + 76, y1 + 3.5, extended ? 15 : 12, 6, setting.getColor().getRGB());
		if (!extended) {

		} else {
			Render2D.drawRectWH(matrices, x, y, sbWidth + 10, hueHeight, 0xFF282828);
			Render2D.fillGradient(matrices, x, y, x + sbWidth, y + hueHeight, 0xFFFFFFFF,
					Color.getHSBColor(setting.getHue(), 1.0f, 1.0f).getRGB());
			for (float i = -2; i + 1 < sbWidth; i += 0.5f) {
				for (float i2 = -2; i2 + 1 < hueHeight; i2 += 1f) {
					int posx = (int) (x + i);
					if (mouseX == posx) {
						if (pressedSB) {
							saturation = (float) (i / sbWidth);
							setting.setSaturation(saturation);
						}
					}
					int posy = (int) (y + i2);
					if (mouseY == posy) {
						if (pressedSB) {
							brightness = (float) (i2 / hueHeight);
							setting.setBrightness(brightness);
						}
					}
					if (0.001 * Math.floor((i2 / hueHeight) * 1000.0) == 0.001 * Math.floor(brightness * 1000.0))
						posY = i2;
				}
				if (0.001 * Math.floor((i / sbWidth) * 1000.0) == 0.001 * Math.floor(saturation * 1000.0))
					posX = i;
			}
			for (float i = -2; i + 1 < hueHeight - 3; i += 0.5f) {
				int posy = (int) (y + i);
				int color = Color.getHSBColor((float) (i / hueHeight), 1.0f, 1.0f).getRGB();
				Render2D.drawRectWH(matrices, x + 72, posy + 3.5, 7, 1, color);
				if (mouseY == posy) {
					if (pressedhue) {
						hue = (float) (i / hueHeight);
						setting.setHue(hue);
					}
				}
				if (0.001 * Math.floor((i / hueHeight) * 1000.0) == 0.001 * Math.floor(hue * 1000.0))
					Render2D.drawRectWH(matrices, x + 71.5, y + i, 8.5, 2, 0xffffffff);
			}
			Render2D.drawUnfilledCircle(x + posX, y + posY, 3f, 1f, -1);
		}

	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (!extended) {
				if (extendedHovered) {
					extended = true;
				}
			} else {
				if (this.hueHovered) {
					pressedhue = true;
					return;
				}
				if (hovered) {
					pressedSB = true;
					return;
				}
				if (noExtHover)
					return;
				extended = false;
			}
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		pressedhue = false;
		pressedSB = false;
	}
	
	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

}
