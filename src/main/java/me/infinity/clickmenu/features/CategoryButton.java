package me.infinity.clickmenu.features;

import java.util.ArrayList;

import me.infinity.InfMain;
import me.infinity.clickmenu.Panel;
import me.infinity.clickmenu.config.ConfigButton;
import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.utils.TimeHelper;
import net.minecraft.client.util.math.MatrixStack;

public class CategoryButton {

	private ArrayList<ModuleButton> modButton = new ArrayList<>();
	private ConfigButton configButton;
	private String name;
	private Panel panel;
	private boolean hovered;
	public boolean configOpen;
	public boolean displayModulePanel;
	private boolean modHovered;
	private int offset;
	private int offsetY;
	private int height;

	// desc timer
	private TimeHelper descTime = new TimeHelper();

	public CategoryButton(String name, Panel panel) {
		this.name = name;
		this.panel = panel;
		for (me.infinity.features.Module module : InfMain.getModuleManager().getList()) {
			if (module.getCategory().name() == name) {
				modButton.add(new ModuleButton(module, module.getName(), this));
			} else if (name == "CONFIGS") {
				configButton = new ConfigButton(panel);
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height, double yMod, double setX, double setY, double setWidth, double setHeight) {
		this.modHovered = Render2D.isHovered(mouseX, mouseY, setX + 65, yMod + 5, width + 93, setHeight - 8);
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		if (displayModulePanel) {
			Render2D.drawRectWH(matrices, x - 1, y - 1, width + 3, height + 2, ColorUtils.lineColor);
			Render2D.drawRectWH(matrices, x, y, width + 6, height, ColorUtils.backNight);
		}
		Render2D.drawRectWH(matrices, x, y, 0.5, height, 0xFF989494);
		Render2D.drawRectWH(matrices, x + 0.5, y, width, height,
				displayModulePanel ? ColorUtils.backNight : hovered ? ColorUtils.SELECT : 0x50000000);
		FontUtils.drawHVCenteredString(matrices, getName(), x + 30, y + 11,
				displayModulePanel ? ColorUtils.openColor : -1);
		if (displayModulePanel) {
			double xOffset = 2;
			double yOffset = 2;
			for (ModuleButton modButton : modButton) {

				if (modButton.open) {
					Render2D.drawRectWH(matrices, setX + 241, setY + 2, width + 97, setHeight - 4,
							ColorUtils.lineColor);
					Render2D.drawRectWH(matrices, setX + 242, setY + 3, width + 95, setHeight - 6,
							ColorUtils.backNight);
				}

				if (modHovered && offsetY > this.height) {
					double border = offsetY / 3;
					Render2D.drawRectWH(matrices, setX + 233, setY + 5, 2.4, setHeight - 10, 0xFF505050);
					Render2D.drawRectWH(matrices, setX + 233, setY + 5 + offset, 2.4, setHeight - 10 - border,
							0xFFD2D2D2);
				}

			}
			if (getName() == "CONFIGS") {
				configButton.render(matrices, mouseX, mouseY, delta, xOffset, yOffset, width, height, yMod, setX, setY,
						setWidth, setHeight);
			} else {
				Render2D.startMenuScissor(setX + 65, yMod + 5, width + 113, setHeight - 8);
				for (ModuleButton modButton : modButton) {
					this.height = (int) setHeight;
					this.offsetY = (int) (yOffset + height);
					modButton.render(matrices, mouseX, mouseY, delta, xOffset + x + 64, yOffset + yMod + 5 - offset,
							width + 22, height - 3, setX, setY, setWidth, setHeight);
					xOffset += 84;
					if (xOffset > 120) {
						xOffset = 2;
						yOffset += 19;
					}
				}
				Render2D.stopScissor();
			}

		}
	}

	private float getCurrentHeight() {
		float cHeight = 0;
		for (ModuleButton modButton : modButton) {
			cHeight += modButton.calcHeight;
		}
		return cHeight;
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hovered) {
			if (button == 0) {
				this.displayModulePanel = !this.displayModulePanel;
				for (CategoryButton b : panel.getCatButton()) {
					if (!b.getName().equalsIgnoreCase(getName()))
						b.displayModulePanel = false;
				}
			}
		}
		if (displayModulePanel) {
			if (getName() == "CONFIGS") {
				configButton.mouseClicked(mouseX, mouseY, button);
			} else {
				modButton.forEach(modButton -> modButton.mouseClicked(mouseX, mouseY, button));
			}
		}
	}

	public void mouseScrolled(double d, double e, double amount) {
		if (displayModulePanel) {
			modButton.forEach(modButton -> modButton.mouseScrolled(d, e, amount));
			if (modHovered) {
				if (amount < 0) {
					if (offsetY > height) {
						this.offset += 35;
						double border = getCurrentHeight();
						if (this.offset > border) {
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

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (displayModulePanel) {
			modButton.forEach(modButton -> modButton.mouseReleased(mouseX, mouseY, button));
		}
	}

	public void charTyped(char chr, int keyCode) {
		if (displayModulePanel) {
			if (getName() == "CONFIGS") {
				configButton.charTyped(chr, keyCode);
			}
		}
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (displayModulePanel) {
			if (getName() == "CONFIGS") {
				configButton.keyPressed(keyCode, scanCode, modifiers);
			}
		}
	}

	public String getName() {
		return name;
	}

	public ArrayList<ModuleButton> getModButton() {
		return modButton;
	}

}