package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;
import org.infinity.utils.MathAssist;
import org.lwjgl.glfw.GLFW;

import net.minecraft.util.math.MathHelper;

public class DoubleSlider extends SliderElement {

	public DoubleSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}
	
	@Override
	public void init() {
		stringAnimation = stringAnimation + (setting.getCurrentValueDouble() - stringAnimation) / 3;
		valueField.setText(getRenderValue());
	}

	@Override
	public void tick() {
		super.tick();

		double currentPos = (setting.getCurrentValueDouble() - setting.getMinValueDouble())
				/ (setting.getMaxValueDouble() - setting.getMinValueDouble());

		animation = animation + (currentPos - animation) / 4;
	}

	@Override
	public String getRenderValue() {
		double value = MathAssist.round(setting.getCurrentValueDouble(), 1);
		return String.valueOf(value);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		double diff = setting.getMaxValueDouble() - setting.getMinValueDouble();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		double val = setting.getMinValueDouble() + percentBar * diff;

		this.setting.setCurrentValueDouble(val);
		valueField.setText(String.valueOf(getRenderValue()));
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (valueField.getText().isEmpty())
			return;

		if (valueField.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ENTER) {
			try {
				setting.setCurrentValueDouble(Double.parseDouble(valueField.getText()));
				
			} catch (NumberFormatException e) {
			}
		}
	}
}
