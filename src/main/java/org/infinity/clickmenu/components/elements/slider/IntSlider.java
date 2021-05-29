package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;
import org.infinity.utils.StringUtil;
import org.lwjgl.glfw.GLFW;

import net.minecraft.util.math.MathHelper;

public class IntSlider extends SliderElement {

	public IntSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}
	
	@Override
	public void init() {
		valueField.setText(getRenderValue());
	}

	@Override
	public void tick() {
		super.tick();

		int currentPos = (setting.getCurrentValueInt() - setting.getMinValueInt())
				/ (setting.getMaxValueInt() - setting.getMinValueInt());

		animation = animation + (currentPos - animation) / 2;
		stringAnimation = stringAnimation
				+ (Math.round(setting.getCurrentValueInt() * 100) / 100 - stringAnimation) / 2;
	}

	@Override
	public String getRenderValue() {
		int value = (int) (Math.round(stringAnimation * 1000) / 1000);
		return StringUtil.DF(value, 0);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		int diff = setting.getMaxValueInt() - setting.getMinValueInt();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		int val = (int) (setting.getMinValueInt() + percentBar * diff);

		this.setting.setCurrentValueInt(val);
		valueField.setText(this.getRenderValue());
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (valueField.getText().isEmpty())
			return;
		if (valueField.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ENTER) {
			try {
				setting.setCurrentValueInt(Integer.parseInt(valueField.getText()));
			} catch (NumberFormatException e) {
			}
		}
	}
}
