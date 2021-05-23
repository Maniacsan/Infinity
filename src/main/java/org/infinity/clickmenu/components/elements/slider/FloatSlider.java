package org.infinity.clickmenu.components.elements.slider;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.elements.SliderElement;
import org.infinity.features.Setting;
import org.infinity.utils.StringUtil;

import net.minecraft.util.math.MathHelper;

public class FloatSlider extends SliderElement {

	public FloatSlider(Setting setting, Panel panel) {
		super(setting, panel);
	}
	
	@Override
	public void tick() {
		super.tick();

		float currentPos = (setting.getCurrentValueFloat() - setting.getMinValueFloat())
				/ (setting.getMaxValueFloat() - setting.getMinValueFloat());

		if (currentPos < 0)
			currentPos = 0;

		animation = animation + (currentPos - animation) / 4F;
		stringAnimation = stringAnimation
				+ (Math.round(setting.getCurrentValueFloat() * 100F) / 100F - stringAnimation) / 2F;
	}

	@Override
	public String getRenderValue() {
		float value = Math.round(stringAnimation * 100.0F) / 100.0F;
		return StringUtil.DF(value, 1);
	}

	@Override
	public void setValue(int mouseX, double x, double width) {
		float diff = setting.getMaxValueFloat() - setting.getMinValueFloat();
		float percentBar = (float) MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
		float val = (float) (setting.getMinValueFloat() + percentBar * diff);

		this.setting.setCurrentValueFloat(val);
	}
}
