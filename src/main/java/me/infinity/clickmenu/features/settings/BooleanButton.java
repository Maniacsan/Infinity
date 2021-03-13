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

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		super.render(matrices, mouseX, mouseY, delta, x, y, width, height);
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);

		Render2D.drawRectWH(matrices, x + 2, y + 0.2, 10, 10, ColorUtils.shadow);
		Render2D.drawRectWH(matrices, x + 2.8, y + 1, 8, height - 9,
				setting.isToggle() ? ColorUtils.booleanToogle : ColorUtils.CHECK_BG);
		FontUtils.drawStringWithShadow(matrices, this.setting.getName(), x + 18, y + 1.3, Color.WHITE.getRGB());
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hovered && button == 0) {
			this.setting.setToggle(!this.setting.isToggle());
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

}
