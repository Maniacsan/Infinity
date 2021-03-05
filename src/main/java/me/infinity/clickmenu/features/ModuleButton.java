package me.infinity.clickmenu.features;

import java.util.ArrayList;
import java.util.List;

import me.infinity.clickmenu.features.settings.BlocksButton;
import me.infinity.clickmenu.features.settings.BooleanButton;
import me.infinity.clickmenu.features.settings.ColorButton;
import me.infinity.clickmenu.features.settings.ModeStringButton;
import me.infinity.clickmenu.features.settings.SettingButton;
import me.infinity.clickmenu.features.settings.SliderButton;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Module;
import me.infinity.features.Settings;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleButton {

	private ArrayList<SettingButton> settingButton = new ArrayList<>();
	private CategoryButton catBut;
	public Module module;
	private String name;
	private boolean hovered;
	private boolean setHovered;
	public double calcHeight;
	private int offset;
	private int offsetY;
	private int height;
	public boolean open;

	public ModuleButton(Module module, String name, CategoryButton catBut) {
		this.module = module;
		this.name = name;
		this.catBut = catBut;
		List<Settings> settings = this.module.getSettings();
		if (settings != null) {
			for (Settings setting : settings) {
				if (setting.isBoolean()) {
					this.settingButton.add(new BooleanButton(setting));
				} else if (setting.isMode()) {
					this.settingButton.add(new ModeStringButton(setting));
				} else if (setting.isValueDouble() || setting.isValueFloat() || setting.isValueInt()) {
					this.settingButton.add(new SliderButton(setting));
				} else if (setting.isBlock()) {
					this.settingButton.add(new BlocksButton(setting));
				} else if (setting.isColor()) {
					this.settingButton.add(new ColorButton(setting));
				}
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height, double setX, double setY, double setWidth, double setHeight) {
		this.calcHeight = height / 4;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		this.setHovered = Render2D.isHovered(mouseX, mouseY, setX + 224, setY + 6, setWidth, setHeight - 8);
		Render2D.drawRectWH(matrices, x, y, width, height, 0xFF706D6D);
		Render2D.drawRectWH(matrices, x, y + 0.5, width, height,
				module.isEnabled() ? 0xFF2A2C2A : hovered ? 0xFF414040 : 0xFF343434);
		FontUtils.drawStringWithShadow(matrices, name, x + 5, y + 5, module.isEnabled() ? 0xFF1AB41E : -1);
		if (!this.module.getSettings().isEmpty()) {
			Render2D.drawRightTriangle((int) x + 68, (int) ((int) y + height / 2 + 2), 4, open ? -1 : 0xFF7A7979);
		}
		Render2D.startMenuScissor(setX + 224, setY + 6, setWidth, setHeight - 8);
		if (open) {
			double yOffset = 2;
			double xOffset = 0;
			if (setHovered && offsetY > this.height) {
				Render2D.drawRectWH(matrices, setX + 372, setY + 5, 2.4, setHeight - 10, 0xFF505050);
				Render2D.drawRectWH(matrices, setX + 372, setY + 5 + offset, 2.4, setHeight - 10 - getCurrentHeight(),
						0xFFD2D2D2);
			}
			for (SettingButton setBut : settingButton) {
				if (setBut.isVisible()) {
					this.height = (int) setHeight;
					this.offsetY = (int) (yOffset + setY + 6);
					setBut.render(matrices, mouseX, mouseY, delta, xOffset + setX + 224, yOffset + setY + 6 - offset,
							width + 30, height);
					if (setBut instanceof BooleanButton) {
						yOffset += 15;
					} else if (setBut instanceof BlocksButton) {
						yOffset += 45;
					} else if (setBut instanceof ModeStringButton) {
						yOffset += 21;
					} else {
						yOffset += 19;
					}
				}
			}
		}
		Render2D.stopScissor();
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
				if (setBut.isVisible()) 
					setBut.mouseClicked(mouseX, mouseY, button);
			}
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (open) {
			for (SettingButton setBut : settingButton) {
				if (setBut.isVisible())
					setBut.mouseReleased(mouseX, mouseY, button);
			}
		}
	}

	private float getCurrentHeight() {
		float cHeight = 0;
		for (SettingButton setBut : settingButton) {
			if (setBut.isVisible()) {
				if (setBut instanceof BlocksButton) {
					cHeight += ((BlocksButton) setBut).y;
				} else
					cHeight += setBut.height;
			}
		}
		return cHeight;
	}

	public void mouseScrolled(double d, double e, double amount) {
		if (open && setHovered) {
			if (amount < 0) {
				if (offsetY > height) {
					this.offset += 35;
					if (this.offset > getCurrentHeight()) {
						this.offset = (int) getCurrentHeight();
					}
				}
			} else if (amount > 0) {
				this.offset -= 35;
				if (this.offset < 0) {
					this.offset = 0;
				}
			}
		}
	}

}
