package me.infinity.clickmenu.features;

import java.util.ArrayList;
import java.util.List;

import me.infinity.clickmenu.features.settings.BlocksButton;
import me.infinity.clickmenu.features.settings.BooleanButton;
import me.infinity.clickmenu.features.settings.ModeStringButton;
import me.infinity.clickmenu.features.settings.SettingButton;
import me.infinity.clickmenu.features.settings.SliderButton;
import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Module;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleButton {

	private ArrayList<SettingButton> settingButton = new ArrayList<>();
	private CategoryButton catBut;
	private Module module;
	private String name;
	private boolean hovered;
	public boolean open;
	private double x;
	private double y;

	public ModuleButton(Module module, String name, CategoryButton catBut) {
		this.module = module;
		this.name = name;
		this.catBut = catBut;
		List<Settings> settings = this.module.getSettings();
		double xOff = 2;
		double yOff = 2;
		if (settings != null) {
			for (Settings setting : settings) {
				if (setting.isBoolean()) {
					this.settingButton.add(new BooleanButton(setting));
				} else if (setting.isMode()) {
					this.settingButton.add(new ModeStringButton(setting));
				} else if (setting.isValueDouble() || setting.isValueFloat() || setting.isValueInt()) {
					this.settingButton.add(new SliderButton(setting));
				} else if (setting.isBlock()) {
					this.settingButton.add(new BlocksButton(setting, xOff + this.x, yOff + this.y, 20, 20));
					yOff += 40;
				}
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height, double setX, double setY, double setWidth, double setHeight) {
		this.x = x;
		this.y = y;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		Render2D.drawRectWH(matrices, x, y, width, height, 0xFF161616);
		Render2D.drawRectWH(matrices, x + 1.5, y + 1.5, width - 3, height - 3,
				module.isEnabled() ? ColorUtils.CHECK_TOGGLE : hovered ? 0xFF414040 : 0xFF222020);
		FontUtils.drawHVCenteredString(matrices, name, x + 34, y + 9, -1);
		if (open) {
			double yOffset = 2;
			for (SettingButton setBut : settingButton) {
				setBut.render(matrices, mouseX, mouseY, delta, setX + 212, yOffset + setY + 6, width + 30, height);
				yOffset += 19;
			}
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered) {
			if (button == 0) {
				module.enable();
			} else if (button == 1) {
				open = !open;
				for (ModuleButton b : catBut.getModButton()) {
					if (!b.name.equalsIgnoreCase(name))
						b.open = false;
				}
			}
		}
		if (open) {
			for (SettingButton setBut : settingButton) {
				setBut.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (open) {
			for (SettingButton setBut : settingButton) {
				setBut.mouseReleased(mouseX, mouseY, button);
			}
		}
	}

}
