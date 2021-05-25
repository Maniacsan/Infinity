package org.infinity.clickmenu.components.buttons;

import java.util.ArrayList;
import java.util.List;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.components.elements.BlocksSelectElement;
import org.infinity.clickmenu.components.elements.CheckBoxElement;
import org.infinity.clickmenu.components.elements.ColorPickerElement;
import org.infinity.clickmenu.components.elements.ModeStringElement;
import org.infinity.clickmenu.components.elements.slider.DoubleSlider;
import org.infinity.clickmenu.components.elements.slider.FloatSlider;
import org.infinity.clickmenu.components.elements.slider.IntSlider;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Module;
import org.infinity.features.Setting;

import net.minecraft.client.util.math.MatrixStack;

public class ModuleButton {

	private ArrayList<AbstractElement> elements = new ArrayList<>();
	private ArrayList<ModuleButton> buttons;
	private Panel panel;
	private Module module;

	private boolean scrollHover;
	private boolean hovered;
	private boolean open;

	private int offset;
	private int _celementHeight;

	private int alpha;

	private double x;
	private double y;
	private double width;
	private double height;

	public ModuleButton(Module module, ArrayList<ModuleButton> buttons, Panel panel) {
		this.module = module;
		this.buttons = buttons;
		this.panel = panel;

		init();
	}

	private void init() {
		List<Setting> settings = getModule().getSettings();
		if (settings == null)
			return;

		for (Setting setting : settings) {
			switch (setting.getCategory()) {
			case "Boolean":
				this.elements.add(new CheckBoxElement(setting, panel));
				break;

			case "String":
				this.elements.add(new ModeStringElement(setting, panel));
				break;

			case "Double":
				this.elements.add(new DoubleSlider(setting, panel));
				break;

			case "Float":
				this.elements.add(new FloatSlider(setting, panel));
				break;

			case "Int":
				this.elements.add(new IntSlider(setting, panel));
				break;

			case "Blocks":
				this.elements.add(new BlocksSelectElement(setting, panel));
				break;

			case "Color":
				this.elements.add(new ColorPickerElement(setting, panel));
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		this.scrollHover = Render2D.isHovered(mouseX, mouseY, panel.x + 244, panel.y + 37, panel.width,
				panel.height - 40);

		Render2D.drawRectWH(matrices, x, y, width, height, 0xFF1E212E);
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 2, height - 2, 0xFF252A40);

		FontUtils.drawStringWithShadow(matrices, module.getName(), x + 5, y + 5, module.isEnabled() ? 0xFF1AB41E : -1);

		double yOffset = 2;
		Render2D.startMenuScissor(panel.x + 224, panel.y + 37, panel.width, panel.height - 40);
		if (isOpen()) {

			if (scrollHover && _celementHeight > panel.height) {
				Render2D.drawRectWH(matrices, panel.x + panel.width - 6, panel.y + 37, 2, panel.height - 40,
						0x90000000);
				Render2D.drawRectWH(matrices, panel.x + panel.width - 6, panel.y + 37 + offset, 2,
						panel.height - 40 - getHeightDifference(), 0xFF1F5A96);
			}

			for (AbstractElement element : elements) {
				if (!element.isVisible())
					continue;

				_celementHeight = (int) (panel.y + 36 + yOffset);
				element.setX(panel.x + 244);
				element.setY(yOffset - offset + panel.y + 36);
				element.setWidth(92);
				element.setHeight(17);

				element.render(matrices, mouseX, mouseY, delta);

				if (element instanceof CheckBoxElement)
					yOffset += 15;
				else if (element instanceof ModeStringElement)
					yOffset += 21;
				else
					yOffset += 19;
			}
		}
		Render2D.stopScissor();
	}

	public void tick() {
		if (isOpen())
			elements.forEach(AbstractElement::tick);
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered) {
			if (button == 0) {
				module.enable();
			} else if (button == 1) {
				setOpen(!isOpen());
				resetAnimation();

				for (ModuleButton b : this.buttons) {
					if (!b.module.getName().equalsIgnoreCase(module.getName()))
						b.setOpen(false);
				}
			}
		}
		if (!isOpen())
			return;

		elements.forEach(element -> {
			if (element.isVisible())
				element.mouseClicked(mouseX, mouseY, button);
		});
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (isOpen()) {
			elements.forEach(element -> {
				if (element.isVisible())
					element.mouseReleased(mouseX, mouseY, button);
			});
		}
	}

	public void mouseScrolled(double d, double e, double amount) {
		int scrollOffset = 15;
		if (!isOpen() || !scrollHover || _celementHeight < panel.height)
			return;

		if (amount < 0.0D) {
			this.offset += scrollOffset;

		} else if (amount > 0.0D) {
			this.offset -= scrollOffset;
		}

		int difference = getHeightDifference();
		if (offset > difference)
			offset = difference;
		else if (offset < 0)
			offset = 0;

	}

	public void onClose() {
		elements.forEach(AbstractElement::onClose);
		resetAnimation();
	}

	public int getElementsHeight() {
		int elementsHeight = 0;
		for (AbstractElement element : elements)
			if (isOpen() && element.isVisible())
				elementsHeight += (element.getHeight());
		return elementsHeight;
	}

	public int getHeightDifference() {
		return (int) (this.getElementsHeight() - (panel.height - 40));
	}

	public void resetAnimation() {
		elements.forEach(element -> {
			element.setAnimation(0);
			element.setStringAnimation(0);
		});
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public Module getModule() {
		return module;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
