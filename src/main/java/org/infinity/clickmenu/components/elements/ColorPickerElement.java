package org.infinity.clickmenu.components.elements;

import java.awt.Color;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Setting;

import net.minecraft.client.util.math.MatrixStack;

public class ColorPickerElement extends AbstractElement {

	private boolean extended;
	private float hue, saturation, brightness;
	private boolean pressedhue;
	private boolean pressedSB;
	private boolean extendedHovered;
	private boolean hovered;
	private boolean hueHovered;
	private boolean noExtHover;
	private double posX, posY, huePos;

	public ColorPickerElement(Setting setting, Panel panel) {
		super(setting, panel);
		float[] hsb = new float[3];
		Color clr = setting.getColor();
		hsb = Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
		this.hue = hsb[0];
		this.saturation = hsb[1];
		this.brightness = hsb[2];
		
		updatePos();
	}

	private void updatePos() {
		double hueHeight = 60;
		double sbWidth = 70;
		for (float i = -2; i + 1 < sbWidth - 1; i += 0.5f) {
			for (float i2 = -2; i2 + 1 < hueHeight - 1; i2 += 0.5f) {
				if (0.001 * Math.floor((i2 / hueHeight) * 1000.0) == 0.001 * Math.floor(brightness * 1000.0))
					posY = i2;
			}
			if (0.001 * Math.floor((i / sbWidth) * 1000.0) == 0.001 * Math.floor(saturation * 1000.0))
				posX = i;
		}
		setting.setSaturation(saturation);
		setting.setBrightness(brightness);

		for (float i = -2; i + 1 < hueHeight - 3; i += 0.5f) {

			if (0.001 * Math.floor((i / hueHeight) * 1000.0) == 0.001 * Math.floor(hue * 1000.0))
				huePos = i;
		}
		
		setting.setHue(hue);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		double hueHeight = 60;
		double sbWidth = 70;
		double x = this.x + 111;
		double y = this.y + 4;
		noExtHover = Render2D.isHovered(mouseX, mouseY, x, y, sbWidth + 12, hueHeight + 2);
		extendedHovered = Render2D.isHovered(mouseX, mouseY, this.x, this.y, width, height);
		hovered = Render2D.isHovered(mouseX, mouseY, x, y, sbWidth - 1, hueHeight);
		hueHovered = Render2D.isHovered(mouseX, mouseY, x + 73, y, 7, hueHeight);
		FontUtils.drawStringWithShadow(matrices, setting.getName(), this.x, this.y + 2, -1);
		Render2D.drawRectWH(matrices, this.x + 95.5, this.y + 1, extended ? 17 : 12, 11, 0xFFB8BEBC);
		Render2D.drawRectWH(matrices, this.x + 96, this.y + 1.5, extended ? 15 : 11, 10, setting.getColor().getRGB());
		if (!extended) {
			pressedhue = false;
			pressedSB = false;
		} else {
			Render2D.drawRectWH(matrices, x - 3, y - 3, sbWidth + 15, hueHeight + 7, setting.getColor().getRGB());
			Render2D.drawRectWH(matrices, x - 2, y - 2, sbWidth + 13, hueHeight + 5, 0xFF282828);
			Render2D.fillGradient(matrices, x, y, x + sbWidth, y + hueHeight, 0xFFFFFFFF,
					Color.getHSBColor(hue, 1.0f, 1.0f).getRGB());
			for (float i = 0; i < sbWidth - 1; i += 0.5f) {
				for (float i2 = 0; i2 < hueHeight - 1; i2 += 0.5f) {
					int posx = (int) ((x) + i);
					if (mouseX == posx) {
						if (pressedSB) {
							saturation = (float) (i / sbWidth);
							setting.setSaturation(saturation);
						}
					}
					int posy = (int) ((y) + i2);
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
					huePos = i;
			}
			Render2D.drawRectWH(matrices, x + 71.5, y + huePos, 8.5, 2, 0xffffffff);
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
				if (this.hueHovered && extended) {
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
	public void mouseScrolled(double d, double e, double amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

	@Override
	public void onClose() {
		pressedhue = false;
		pressedSB = false;
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
}
