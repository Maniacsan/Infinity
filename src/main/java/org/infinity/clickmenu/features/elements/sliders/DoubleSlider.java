package org.infinity.clickmenu.features.elements.sliders;

import org.infinity.clickmenu.features.elements.SliderElement;
import org.infinity.features.Setting;

import net.minecraft.util.math.MathHelper;

public class DoubleSlider extends SliderElement {

	public DoubleSlider(Setting setting) {
		super(setting);
		this.selected = setting.getCurrentValueDouble() / setting.getMaxValueDouble();
	}

	@Override
	public String getRenderValue() {
		double value = Math.round(setting.getCurrentValueDouble() * 100.0) / 100.0;
		return String.valueOf(value);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		double diff = setting.getMaxValueDouble() - setting.getMinValueDouble();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		double val = setting.getMinValueDouble() + percentBar * diff;

		this.setting.setCurrentValueDouble(val);
		this.selected = percentBar;
	}
}
