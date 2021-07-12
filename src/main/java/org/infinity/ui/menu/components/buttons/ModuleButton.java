package org.infinity.ui.menu.components.buttons;

import java.util.ArrayList;
import java.util.List;

import org.infinity.features.Module;
import org.infinity.features.Setting;
import org.infinity.features.Setting.Category;
import org.infinity.font.IFont;
import org.infinity.ui.menu.components.Panel;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.components.elements.BlocksSelectElement;
import org.infinity.ui.menu.components.elements.CheckBoxElement;
import org.infinity.ui.menu.components.elements.ColorPickerElement;
import org.infinity.ui.menu.components.elements.ComboBoxElement;
import org.infinity.ui.menu.components.elements.SliderElement;
import org.infinity.ui.menu.components.elements.slider.DoubleSlider;
import org.infinity.ui.menu.components.elements.slider.FloatSlider;
import org.infinity.ui.menu.components.elements.slider.IntSlider;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

public class ModuleButton {

	private ArrayList<AbstractElement> elements = new ArrayList<>();
	private ArrayList<ModuleButton> buttons;
	private Timer scrollTimer = new Timer();

	private Panel panel;
	private Module module;

	private boolean scrollHover;
	private boolean hovered;
	private boolean open;
	private boolean keyOpen;

	private double scrollSpeed;
	private double prevScrollProgress;
	private double scrollProgress;
	private int _celementHeight;

	private int alpha;

	private double x;
	private double y;
	private double width;
	private double height;

	private double hoverAnim;

	private float fadeAlpha;

	public ModuleButton(Module module, ArrayList<ModuleButton> buttons, Panel panel) {
		this.module = module;
		this.buttons = buttons;
		this.panel = panel;

		initElements();
	}

	public void init() {
		elements.forEach(element -> element.init());
	}

	public void addChildren(List<Element> children) {
		elements.forEach(element -> {
		});
	}

	private void initElements() {
		List<Setting> settings = getModule().getSettings();
		if (settings == null)
			return;

		for (Setting setting : settings) {
			switch ((Category) setting.getCategory()) {
			case COLOR:
				this.elements.add(new ColorPickerElement(setting));
				break;

			case MODE:
				this.elements.add(new ComboBoxElement(setting));
				break;

			case BOOLEAN:
				this.elements.add(new CheckBoxElement(setting));
				break;

			case VALUE_DOUBLE:
				this.elements.add(new DoubleSlider(setting));
				break;

			case VALUE_FLOAT:
				this.elements.add(new FloatSlider(setting));
				break;

			case VALUE_INT:
				this.elements.add(new IntSlider(setting));
				break;

			case BLOCKS:
				this.elements.add(new BlocksSelectElement(setting));
				break;
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		this.scrollHover = Render2D.isHovered(mouseX, mouseY, panel.x + 244, panel.y + 37, panel.width,
				panel.height - 40);

		fadeAlpha = (float) (isOpen() ? Math.min(1, fadeAlpha + 0.16) : 0);
		hoverAnim = hovered ? Math.min(2.7, hoverAnim + 0.18) : Math.max(1, hoverAnim - 0.15);

		// shadow
		Render2D.drawRectWH(matrices, x + width - 3, y + 1, 2, height - 6, 0x700E1015);
		Render2D.verticalGradient(matrices, x + width - 3, y + height - 5, x + width - 1, y + height + hoverAnim,
				0x900E1015, 0x00000000);
		Render2D.verticalGradient(matrices, x + 1, y + height - 5, x + width - 3, y + height + hoverAnim, 0xFF0E1015,
				0x00000000);

		// button rect
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 3, height - 3, 0xFF242C41);

		if (!module.getSettings().isEmpty()) {
			Render2D.drawRectWH(matrices, x + width - 10, y + 1, 8, height - 3, 0xFF1F273B);
			RenderUtil.drawImage(matrices, true, x + width - 15, y + 3, 20, 20,
					"/assets/infinity/textures/icons/dots.png", 0xFFD4D4D4);
		}

		if (keyOpen) {
			String key = String.valueOf(InputUtil.fromKeyCode(module.getKey(), module.getKey()))
					.replace("key.keyboard.", "").toUpperCase();
			IFont.legacy12.drawString(matrices, "Press SPACE to remove binds", x + 4, y + 15, 0xFFA6A19A);
			IFont.legacy15.drawString(matrices, module.getKey() == -2 ? "Key: " + "..." : "Key: " + key, x + 4, y + 5,
					0xFFFFFFFF);
		} else
			IFont.legacy17.drawString(matrices, module.getName(), x + 4, y + 7,
					module.isEnabled() ? 0xFF3F80FB : 0xFFFFFFFF);

		double yOffset = 2;
		alpha = alpha < 1 ? Math.min(1, alpha + 1) : 100;

		if (isOpen()) {
			Render2D.startScissor(panel.x + 224, panel.y + 37, panel.width, panel.height - 40);
			RenderSystem.setShaderColor(1f, 1f, 1f, fadeAlpha);

			if (scrollHover && _celementHeight > panel.height) {
				Render2D.drawRectWH(matrices, panel.x + panel.width - 3, panel.y + 37, 1, panel.height - 40,
						0x90000000);
				Render2D.drawRectWH(matrices, panel.x + panel.width - 3, panel.y + 37 + getScrollProgress(), 1,
						panel.height - 40 - getHeightDifference(), 0xFF1F5A96);
			}

			for (AbstractElement element : elements) {
				if (!element.isVisible())
					continue;

				_celementHeight = (int) (panel.y + 36 + yOffset);
				element.setX(panel.x + 241);
				element.setY(yOffset - getScrollProgress() + panel.y + 36);
				element.setWidth(panel.width - 264);
				element.setHeight(19);

				element.render(matrices, mouseX, mouseY, delta);

				if (element instanceof SliderElement)
					yOffset += 18;
				else if (element instanceof ColorPickerElement) {
					yOffset += 20;
				} else if (element instanceof ComboBoxElement) {
					if (((ComboBoxElement) element).isOpen())
						yOffset += ((ComboBoxElement) element).getSetting().getModes().size() * 20;
					else
						yOffset += 20;
				} else
					yOffset += 17;
			}
			Render2D.stopScissor();
		}
	}

	public void tick() {
		if (isOpen())
			elements.forEach(AbstractElement::tick);

		if (!isOpen() || _celementHeight < panel.height)
			return;

		int difference = getHeightDifference();

		setScrollProgress(scrollProgress + scrollSpeed);
		scrollSpeed *= 0.54;

		if (scrollTimer.hasReached(100)) {
			if (scrollProgress < 0)
				scrollSpeed = scrollProgress * -0.45;
			else if (scrollProgress > difference)
				scrollSpeed = (scrollProgress - difference) * -0.45;
		}
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
			} else if (button == 2) {
				if (keyOpen)
					keyOpen = false;
				else
					keyOpen = true;
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
		if (amount != 0 && scrollHover && isOpen()) {
			double sa = amount < 0 ? amount - 10 : amount + 10;
			scrollTimer.reset();
			scrollSpeed -= sa;
		}
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (isOpen())
			elements.forEach(element -> element.keyPressed(keyCode, scanCode, modifiers));

		if (keyOpen) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_SPACE
					|| keyCode == GLFW.GLFW_KEY_BACKSPACE) {
				module.setKey(-2);
				Helper.infoMessage(Formatting.GRAY + "[Feature] " + Formatting.WHITE + module.getName()
						+ Formatting.GRAY + " removed binds");
			} else {
				module.setKey(keyCode);
				Helper.infoMessage(Formatting.GRAY + "[Feature] " + Formatting.WHITE + module.getName()
						+ Formatting.GRAY + " binded to " + Formatting.AQUA
						+ String.valueOf(InputUtil.fromKeyCode(keyCode, keyCode)).replace("key.keyboard.", "")
								.toUpperCase());
			}
			keyOpen = false;
		}
	}

	public void charTyped(char chr, int keyCode) {
		elements.forEach(element -> element.charTyped(chr, keyCode));
	}

	public void onClose() {
		elements.forEach(AbstractElement::onClose);
		resetAnimation();
		keyOpen = false;
	}

	public int getElementsHeight() {
		int elementsHeight = 0;
		double offset = -3;
		for (AbstractElement element : elements) {
			if (isOpen() && element.isVisible()) {
				if (element instanceof ComboBoxElement && ((ComboBoxElement) element).isOpen())
					offset = (((ComboBoxElement) element).getSetting().getModes().size() - 1) * 18;
				else if (element instanceof ColorPickerElement)
					offset = 1;
				else
					offset = 0;

				elementsHeight += (element.getHeight() + offset);
			}
		}
		return elementsHeight;
	}

	public int getHeightDifference() {
		return (int) (this.getElementsHeight() - (panel.height - 40));
	}

	public void resetAnimation() {
		elements.forEach(element -> {
			element.setAnimation(0);
			element.setStringAnimation(0);
			fadeAlpha = -0.3f;

			if (element instanceof CheckBoxElement)
				((CheckBoxElement) element).setMove(0);
		});
		alpha = 0;
	}

	private double getScrollProgress() {
		return prevScrollProgress
				+ (scrollProgress - prevScrollProgress) * Helper.minecraftClient.getLastFrameDuration();
	}

	private void setScrollProgress(double value) {
		prevScrollProgress = scrollProgress;
		scrollProgress = value;
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
