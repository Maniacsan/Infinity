package org.infinity.clickmenu.features;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.features.elements.BlocksElement;
import org.infinity.clickmenu.features.elements.BooleanElement;
import org.infinity.clickmenu.features.elements.ColorElement;
import org.infinity.clickmenu.features.elements.ModeStringElement;
import org.infinity.clickmenu.features.elements.sliders.DoubleSlider;
import org.infinity.clickmenu.features.elements.sliders.FloatSlider;
import org.infinity.clickmenu.features.elements.sliders.IntSlider;
import org.infinity.clickmenu.util.ColorUtils;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Module;
import org.infinity.features.Settings;

import net.minecraft.client.util.math.MatrixStack;

public class ModuleButton {

	public ArrayList<SettingElement> elements = new ArrayList<>();
	private CategoryButton catBut;
	public Module module;
	private String name;
	public boolean hovered;
	private boolean setHovered;
	public double calcHeight;
	private int offset;
	private int offsetY;
	private int height;
	public double moduleHeight;
	public boolean open;

	public ModuleButton(Module module, String name, CategoryButton catBut) {
		this.module = module;
		this.name = name;
		this.catBut = catBut;
		List<Settings> settings = this.module.getSettings();
		if (settings != null) {
			for (Settings setting : settings) {
				if (setting.isBoolean()) {
					this.elements.add(new BooleanElement(setting));
				} else if (setting.isMode()) {
					this.elements.add(new ModeStringElement(setting));
				} else if (setting.isValueDouble()) {
					this.elements.add(new DoubleSlider(setting));
				} else if (setting.isValueFloat()) {
					this.elements.add(new FloatSlider(setting));
				} else if (setting.isValueInt()) {
					this.elements.add(new IntSlider(setting));
				} else if (setting.isBlock()) {
					this.elements.add(new BlocksElement(setting));
				} else if (setting.isColor()) {
					this.elements.add(new ColorElement(setting));
				}
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height, double setX, double setY, double setWidth, double setHeight) {
		moduleHeight = height / 2;
		this.calcHeight = height / 4;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		this.setHovered = Render2D.isHovered(mouseX, mouseY, setX + 224, setY + 6, setWidth, setHeight - 8);
		Render2D.drawRectWH(matrices, x, y, width, height, 0xFF706D6D);
		Render2D.drawRectWH(matrices, x, y + 1, width, height - 1, module.isEnabled() ? 0xFF2A2C2A
				: hovered ? ColorUtils.blend(Color.GRAY, Color.WHITE, 0.5).getRGB() : 0xFF343434);
		FontUtils.drawStringWithShadow(matrices, name, x + 5, y + 5, module.isEnabled() ? 0xFF1AB41E : -1);
		if (!this.module.getSettings().isEmpty()) {
			Render2D.drawRightTriangle((int) x + 80, (int) ((int) y + height / 2 + 2), 4, open ? -1 : 0xFF7A7979);
		}

		Render2D.startMenuScissor(setX + 224, setY + 6, setWidth, setHeight - 8);
		if (open) {
			double yOffset = 2;
			double xOffset = 0;

			if (setHovered && offsetY > this.height) {
				Render2D.drawRectWH(matrices, setX + 392, setY + 5, 2.4, setHeight - 10, 0xFF505050);
				Render2D.drawRectWH(matrices, setX + 392, setY + 5 + offset, 2.4,
						setHeight - 10 - getHeightDifference(), 0xFFD2D2D2);
			}

			for (SettingElement element : elements) {
				if (element.isVisible()) {
					this.height = (int) setHeight;
					this.offsetY = (int) (yOffset + setY + 6);
					element.render(matrices, mouseX, mouseY, delta, xOffset + setX + 244, yOffset + setY + 6 - offset,
							width + 30, height);
					if (element instanceof BooleanElement) {
						yOffset += 15;
					} else if (element instanceof BlocksElement) {
						yOffset += 45;
					} else if (element instanceof ModeStringElement) {
						yOffset += 21;
					} else {
						yOffset += 19;
					}
				}
			}

		}
		Render2D.stopScissor();
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered) {
			if (button == 0) {
				module.enable();
			} else if (button == 1) {
				open = !open;
				for (ModuleButton b : catBut.getModButton()) {
					if (!b.name.equalsIgnoreCase(name))
						b.open = false;
				}
			}
		}
		if (open) {
			for (SettingElement element : elements) {
				if (element.isVisible())
					element.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (open) {
			for (SettingElement element : elements) {
				if (element.isVisible())
					element.mouseReleased(mouseX, mouseY, button);
			}
		}
	}

	public int getElementsHeight() {
		int elementsHeight = 0;
		for (SettingElement element : elements)
			if (open && element.isVisible())
				elementsHeight += (element.height + 1);
		return elementsHeight;
	}

	public int getHeightDifference() {
		return (this.getElementsHeight() - this.height);
	}

	public void mouseScrolled(double d, double e, double amount) {
		if (open && setHovered) {
			int difference = this.getHeightDifference();
			int scrollOffset = (this.getElementsHeight() / elements.size());
			if (amount < 0) {
				if (offsetY > height) {
					this.offset += scrollOffset;
					if (this.offset > difference)
						this.offset = difference;
				}
			} else if (amount > 0) {
				this.offset -= scrollOffset;
				if (this.offset < 0)
					this.offset = 0;
			}
		}
	}

	public void onClose() {
		for (SettingElement element : elements) {
			element.onClose();
		}
	}

}
