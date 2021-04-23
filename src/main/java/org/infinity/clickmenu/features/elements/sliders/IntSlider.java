package org.infinity.clickmenu.features.elements.sliders;

import org.infinity.clickmenu.features.elements.SliderElement;
import org.infinity.features.Settings;
import org.infinity.utils.StringUtil;

import net.minecraft.util.math.MathHelper;

public class IntSlider extends SliderElement {

	public IntSlider(Settings setting) {
		super(setting);
		this.selected = this.width / (setting.getMaxValueInt() - setting.getMinValueInt())
				* (setting.getCurrentValueInt() - setting.getMinValueInt());
	}

	@Override
	public String getRenderValue() {
		double value = Math.round(setting.getCurrentValueInt() * 100.0) / 100.0;
		return StringUtil.DF(value, 0);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		int diff = setting.getMaxValueInt() - setting.getMinValueInt();
		double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		int val = (int) (setting.getMinValueInt() + percentBar * diff);

		this.setting.setCurrentValueInt(val);
		this.selected = percentBar;
	}
}
