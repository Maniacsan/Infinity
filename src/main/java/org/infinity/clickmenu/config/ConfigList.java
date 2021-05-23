package org.infinity.clickmenu.config;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.file.config.Config;
import org.infinity.main.InfMain;

import net.minecraft.client.util.math.MatrixStack;

public class ConfigList {

	private ConfigPanel configButton;
	public Config config;
	public boolean select;
	
	private double x;
	private double y;
	private double width;
	private double height;

	public ConfigList(Config config, ConfigPanel configButton) {
		this.config = config;
		this.configButton = configButton;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		if (select) {
			Render2D.drawRectWH(matrices, x, y, width, height, 0x90000000);
			Render2D.drawRectWH(matrices, x, y, 2, height,
					((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).color.getColor().getRGB());
		}
		FontUtils.drawHVCenteredString(matrices, config.getName(), x + 90, y + 6, -1);
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
			select = !select;

			for (ConfigList listButton : configButton.configList) {
				if (!listButton.config.getName().equalsIgnoreCase(config.getName())) {
					listButton.select = false;
				}
			}
		}
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

}
