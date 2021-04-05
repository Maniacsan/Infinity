package me.infinity.clickmenu.features.elements.sliders;

import me.infinity.clickmenu.features.elements.SliderElement;
import me.infinity.features.Settings;
import me.infinity.utils.MathAssist;
import net.minecraft.util.math.MathHelper;

public class DoubleSlider extends SliderElement {

	public DoubleSlider(Settings setting) {
		super(setting);
		this.selected = setting.getCurrentValueDouble() / setting.getMaxValueDouble();
	}

	@Override
	public String getRenderValue() {
		double value = Math.round(setting.getCurrentValueDouble() * 100.0) / 100.0;
		return String.valueOf(MathAssist.round(value, 1));
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
