package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ModeStringButton extends SettingButton {

	private boolean addHovered;
	
	public ModeStringButton(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		super.render(matrices, mouseX, mouseY, delta, x, y, width, height);
		this.addHovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		
		FontUtils.drawStringWithShadow(matrices, setting.getName() + " - " + setting.getCurrentMode(), x + 4, y + 5, Color.WHITE.getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.addHovered) {
			if (button == 0) {
			if (this.setting.isMode()) {
				try {
					this.setting.setCurrentMode(this.setting.getModes().get(this.setting.getCurrentModeIndex() + 1));
				} catch (Exception e) {
					this.setting.setCurrentMode(this.setting.getModes().get(0));
				}
			}
			}
		}
	}

}
