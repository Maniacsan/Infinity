package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class BooleanButton extends SettingButton {

	private boolean hovered;

	public BooleanButton(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		super.render(matrices, mouseX, mouseY, delta, x, y, width, height);
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height - 5);

		Render2D.drawRectWH(matrices, x + 2, y + 0, 10, 10, setting.isToggle() ? ColorUtils.CHECK_TOGGLE : 0xFFFFFFFF);
		Render2D.drawRectWH(matrices, x + 3, y + 1, 8, height - 9, ColorUtils.backNight);
		if (setting.isToggle()) {
			Render2D.drawRectWH(matrices, x + 4, y + 2, 6, 6, ColorUtils.CHECK_TOGGLE);
		}
		FontUtils.drawStringWithShadow(matrices, this.setting.getName(), x + 17, y + 1.5, Color.WHITE.getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hovered && button == 0) {
			this.setting.setToggle(!this.setting.isToggle());
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

}
