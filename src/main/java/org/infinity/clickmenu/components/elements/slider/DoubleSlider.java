package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;

import net.minecraft.util.math.MathHelper;

public class DoubleSlider extends SliderElement {

	public DoubleSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void tick() {
		super.tick();

		double currentPos = (setting.getCurrentValueDouble() - setting.getMinValueDouble())
				/ (setting.getMaxValueDouble() - setting.getMinValueDouble());

		if (currentPos < 0)
			currentPos = 0;

		animation = animation + (currentPos - animation) / 4;
		stringAnimation = stringAnimation
				+ (setting.getCurrentValueDouble() - stringAnimation) / 3;
	}

	@Override
	public String getRenderValue() {
		double value = Math.round(stringAnimation * 100) / 100;
		return String.valueOf(value);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		double diff = setting.getMaxValueDouble() - setting.getMinValueDouble();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		double val = setting.getMinValueDouble() + percentBar * diff;

		this.setting.setCurrentValueDouble(val);
	}
}
