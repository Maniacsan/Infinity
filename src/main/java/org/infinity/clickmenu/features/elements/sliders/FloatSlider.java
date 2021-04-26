package org.infinity.clickmenu.features.elements.sliders;

import org.infinity.clickmenu.features.elements.SliderElement;
import org.infinity.features.Settings;
import org.infinity.utils.StringUtil;

import net.minecraft.util.math.MathHelper;

public class FloatSlider extends SliderElement {

	public FloatSlider(Settings setting) {
		super(setting);
		this.selected = this.width / (setting.getMaxValueFloat() - setting.getMinValueFloat())
				* (setting.getCurrentValueFloat() - setting.getMinValueFloat());
	}

	@Override
	public String getRenderValue() {
		double value = Math.round(setting.getCurrentValueFloat() * 100.0) / 100.0;
		return StringUtil.DF(value, 1);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		float diff = setting.getMaxValueFloat() - setting.getMinValueFloat();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		float val = (float) (setting.getMinValueFloat() + percentBar * diff);

		this.setting.setCurrentValueFloat(val);
		this.selected = percentBar;
	}
}