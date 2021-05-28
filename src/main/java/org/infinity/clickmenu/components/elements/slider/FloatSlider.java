package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;
import org.infinity.utils.MathAssist;
import org.infinity.utils.StringUtil;
import org.lwjgl.glfw.GLFW;

import net.minecraft.util.math.MathHelper;

public class FloatSlider extends SliderElement {

	public FloatSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void init() {
		valueField.setText(getRenderValue());
	}
	
	@Override
	public void tick() {
		super.tick();

		float currentPos = (setting.getCurrentValueFloat() - setting.getMinValueFloat())
				/ (setting.getMaxValueFloat() - setting.getMinValueFloat());

		animation = animation + (currentPos - animation) / 4F;
	}

	@Override
	public String getRenderValue() {
		float value = (float) MathAssist.round(setting.getCurrentValueFloat(), 2);
		return StringUtil.DF(value, 1);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		float diff = setting.getMaxValueFloat() - setting.getMinValueFloat();
		float percentBar = (float) MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		float val = (float) (setting.getMinValueFloat() + percentBar * diff);

		this.setting.setCurrentValueFloat(val);
		valueField.setText(this.getRenderValue());
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (valueField.getText().isEmpty())
			return;
		if (valueField.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ENTER) {
			try {
				setting.setCurrentValueFloat(Float.parseFloat(valueField.getText()));
			} catch (NumberFormatException e) {
			}
		}
	}
}
