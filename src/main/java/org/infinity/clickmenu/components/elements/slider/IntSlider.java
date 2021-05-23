package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;
import org.infinity.utils.StringUtil;

import net.minecraft.util.math.MathHelper;

public class IntSlider extends SliderElement {

	public IntSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}
	
	@Override
	public void tick() {
		super.tick();

		int currentPos = (setting.getCurrentValueInt() - setting.getMinValueInt())
				/ (setting.getMaxValueInt() - setting.getMinValueInt());

		if (currentPos < 0)
			currentPos = 0;

		animation = animation + (currentPos - animation) / 4;
		stringAnimation = stringAnimation
				+ (Math.round(setting.getCurrentValueInt() * 1000) / 1000 - stringAnimation) / 2;
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
	}
}
