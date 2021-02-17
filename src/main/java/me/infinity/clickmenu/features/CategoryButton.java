package me.infinity.clickmenu.features;

import java.util.ArrayList;

import me.infinity.InfMain;
import me.infinity.clickmenu.Panel;
import me.infinity.clickmenu.config.ConfigButton;
import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.module.visual.GuiMod;
import net.minecraft.client.util.math.MatrixStack;

public class CategoryButton {

	private ArrayList<ModuleButton> modButton = new ArrayList<>();
	private ConfigButton configButton;
	private String name;
	private Panel panel;
	private boolean hovered;
	public boolean configOpen;
	public boolean displayModulePanel;

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
		boolean theme = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).theme.getCurrentMode().equalsIgnoreCase("Light");
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
			if (getName() == "CONFIGS") {
				configButton.render(matrices, mouseX, mouseY, delta, xOffset, yOffset, width, height, yMod, setX, setY,
						setWidth, setHeight);
			} else {
				for (ModuleButton modButton : modButton) {
					if (modButton.open) {
						Render2D.drawRectWH(matrices, setX + 210, setY + 2, width + 108, setHeight - 4,
								ColorUtils.lineColor);
						Render2D.drawRectWH(matrices, setX + 211, setY + 3, width + 106, setHeight - 6,
								ColorUtils.backNight);
					}
					modButton.render(matrices, mouseX, mouseY, delta, xOffset + x + 65, yOffset + yMod + 5, width + 6,
							height - 3, setX, setY, setWidth, setHeight);
					xOffset += 68;
					if (xOffset > 120) {
						xOffset = 2;
						yOffset += 19;
					}
				}
			}
		}
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
