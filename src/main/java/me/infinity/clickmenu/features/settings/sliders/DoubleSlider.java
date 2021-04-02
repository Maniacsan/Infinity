package me.infinity.clickmenu.features.settings.sliders;

import me.infinity.clickmenu.features.settings.SettingButton;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class DoubleSlider extends SettingButton {

	private double selected;
	public boolean dragging;
	private boolean hovered;

	public DoubleSlider(Settings setting) {
		super(setting);
		this.selected = setting.getCurrentValueDouble() / setting.getMaxValueDouble();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.height = height;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x + 2, y, width, height);
		final String sname = setting.getName();
		final String setstrg = String.valueOf(String.valueOf(sname.substring(0, 1).toUpperCase()))
				+ sname.substring(1, sname.length());
		final String displayval = new StringBuilder()
				.append(Math.round(setting.getCurrentValueDouble() * 100.0) / 100.0).toString();

		FontUtils.drawStringWithShadow(matrices, setstrg, (float) (x + 2), (float) (y), -1);
		FontUtils.drawHVCenteredString(matrices, displayval, (float) (x + width + 17), (float) (y + 14.0), -1);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width, 2, -2130706433);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width * this.selected, 2, 0xFF79E649);
		Render2D.drawFullCircle(x + 1 + width * selected, y + 13, 3, 0xFFCCD6C8);
		if (this.dragging) {
			final double diff = setting.getMaxValueDouble() - setting.getMinValueDouble();
			final double percentBar = MathHelper.clamp((mouseX - x) / width, 0.0, 1.0);
			final double val = setting.getMinValueDouble() + percentBar * diff;

				setting.setCurrentValueDouble(val);

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

	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

}
