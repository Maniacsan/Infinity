package org.infinity.ui.menu.components.window;

import java.util.ArrayList;

import org.infinity.features.Setting;
import org.infinity.features.Setting.Category;
import org.infinity.features.module.hidden.Menu;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.components.Panel;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.components.elements.BlocksSelectElement;
import org.infinity.ui.menu.components.elements.CheckBoxElement;
import org.infinity.ui.menu.components.elements.ColorPickerElement;
import org.infinity.ui.menu.components.elements.ComboBoxElement;
import org.infinity.ui.menu.components.elements.slider.DoubleSlider;
import org.infinity.ui.menu.components.elements.slider.FloatSlider;
import org.infinity.ui.menu.components.elements.slider.IntSlider;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.utils.Helper;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

// In progress
public class MenuSettingWindow extends Screen {

	private ArrayList<AbstractElement> elements = new ArrayList<>();
	private Screen prev;

	private double anim;
	private double hover;

	private double x, y;

	public MenuSettingWindow(Screen prev, Panel panel) {
		super(new LiteralText(""));
		this.prev = prev;
		anim = 0.4;
	}

	@Override
	public void init() {
		for (Setting setting : InfMain.getModuleManager().get(Menu.class).getSettings()) {
			if (setting == null)
				continue;

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

		x = width / 2 - 100;
		y = height / 2 - 140;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		prev.render(matrices, -1, -1, delta);
		renderPanel(matrices, mouseY, mouseY, delta);
	}

	private void renderPanel(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Render2D.drawBorderedRect(matrices, x, y, 200, 280, 1, 0xFF080629, 0xFF161621);

		double yOffset = 0;
		for (AbstractElement element : elements) {
			element.setX(x + 10);
			element.setY(y + yOffset + 20);
			element.setWidth(150);
			element.setHeight(20);

			element.render(matrices, mouseX, mouseY, delta);

			yOffset += 20;
		}
	}

	@Override
	public void tick() {
		elements.forEach(AbstractElement::tick);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		elements.forEach(element -> element.mouseReleased(mouseX, mouseY, button));
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		elements.forEach(element -> element.mouseClicked(mouseX, mouseY, button));
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		elements.forEach(element -> element.mouseScrolled(d, e, amount));
		return super.mouseScrolled(d, e, amount);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		return super.charTyped(chr, keyCode);
	}

	@Override
	public void onClose() {
		elements.forEach(AbstractElement::onClose);
		anim = 0.4;
		Helper.openScreen(prev);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		elements.forEach(element -> element.keyPressed(keyCode, scanCode, modifiers));
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

}
