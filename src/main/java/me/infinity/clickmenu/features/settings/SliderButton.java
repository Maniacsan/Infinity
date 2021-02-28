package me.infinity.clickmenu.features.settings;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SliderButton extends SettingButton {

	private double selected;
	public boolean dragging;
	private boolean hovered;

	public SliderButton(Settings setting) {
		super(setting);
		this.selected = setting.isValueInt() ? setting.getCurrentValueInt() / setting.getMaxValueInt()
				: setting.isValueDouble() ? setting.getCurrentValueDouble() / setting.getMaxValueDouble()
						: setting.isValueFloat() ? setting.getCurrentValueFloat() / setting.getMaxValueFloat() : 0;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x + 2, y, width, height);
		final String sname = setting.getName();
		final String setstrg = String.valueOf(String.valueOf(sname.substring(0, 1).toUpperCase()))
				+ sname.substring(1, sname.length());
		final String displayval = new StringBuilder()
				.append(Math.round(setting.getCurrentValueDouble() * 100.0) / 100.0).toString();
		final String floatVal = new StringBuilder().append(Math.round(setting.getCurrentValueFloat() * 100.0) / 100.0)
				.toString();
		final String intVal = new StringBuilder().append(Math.round(setting.getCurrentValueInt())).toString();

		FontUtils.drawStringWithShadow(matrices, setstrg, (float) (x + 2), (float) (y), -1);
		FontUtils.drawHVCenteredString(matrices,
				setting.isValueDouble() ? displayval
						: setting.isValueFloat() ? floatVal : setting.isValueInt() ? intVal : intVal,
				(float) (x + width + 17), (float) (y + 14.0), -1);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width, 2, -2130706433);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width * this.selected, 2, 0xFF79E649);
		Render2D.drawFullCircle(x + 1 + width * selected, y + 13, 3, 0xFFCCD6C8);
		if (this.dragging) {
			final double diff = setting.isValueDouble() ? setting.getMaxValueDouble() - setting.getMinValueDouble()
					: setting.isValueFloat() ? setting.getMaxValueFloat() - setting.getMinValueFloat()
							: setting.isValueInt() ? setting.getMaxValueInt() - setting.getMinValueInt()
									: setting.getMaxValueInt() - setting.getMinValueInt();

			final double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
			final double val = setting.isValueInt() ? setting.getMinValueInt() + percentBar * diff
					: setting.getMinValueDouble() + percentBar * diff;

			if (setting.isValueDouble()) {
				setting.setCurrentValueDouble(val);
			} else if (setting.isValueFloat()) {
				setting.setCurrentValueFloat((float) val);
			} else if (setting.isValueInt()) {
				setting.setCurrentValueInt((int) val);
			}
			this.selected = percentBar;
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && hovered) {
			this.dragging = true;
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.dragging = false;
	}

}
