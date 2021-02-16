package me.infinity.clickmenu.config;

import java.util.ArrayList;

import me.infinity.clickmenu.util.ColorUtils;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.file.config.Config;
import net.minecraft.client.util.math.MatrixStack;

public class ConfigListButton {
	
	public ArrayList<String> checkName = new ArrayList<>();
	private ConfigButton configButton;
	public Config config;
	private boolean hovered;
	public boolean select;
	
	public ConfigListButton(Config config, ConfigButton configButton) {
		this.config = config;
		this.configButton = configButton;
	}
	
	public void render(MatrixStack matrices, int mouseX, int mouseY, double setX, double setY, double setWidth, double setHeight) {
		hovered = Render2D.isHovered(mouseX, mouseY, setX, setY, setWidth, setHeight);
		if (select) {
			Render2D.drawRectWH(matrices, setX, setY, setWidth, setHeight, 0x90000000);
			Render2D.drawRectWH(matrices, setX, setY, 2, setHeight, ColorUtils.CHECK_TOGGLE);
		}
		FontUtils.drawHVCenteredString(matrices, config.getName(), setX + 80, setY + 6, -1);
	}
	
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered && button == 0) {
			select = !select;
			if (select) {
				checkName.add(config.getName());
			} else {
				checkName.clear();
			}
			for (ConfigListButton listButton : configButton.configList) {
				if (!listButton.config.getName().equalsIgnoreCase(config.getName())) {
					listButton.select = false;
					checkName.remove(config.getName());
				}
			}
		}
	}


}
