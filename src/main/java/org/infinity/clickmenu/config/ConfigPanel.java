package org.infinity.clickmenu.config;

import java.util.ArrayList;

import org.infinity.clickmenu.ClickMenu;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WTextField;
import org.infinity.file.config.Config;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ConfigPanel {

	public ArrayList<ConfigList> configList = new ArrayList<>();
	private WTextField textField;
	private String name;
	protected boolean focused;
	private boolean configFull;

	private double x;
	private double y;
	private double width;
	private double height;

	public ConfigPanel(String name) {
		this.name = name;
		refresh();
		configFull = false;
	}
	
	public void init() {
		Helper.minecraftClient.keyboard.setRepeatEvents(true);
		textField = new WTextField(Helper.minecraftClient.textRenderer, (int) this.width / 2 - 80,
				(int) this.height / 2 - 58, 160, 22, new TranslatableText("Text"), false);

		textField.setMaxLength(40);
		textField.setColor(0xFF1D1F30);
	}

	public void refresh() {
		configList.clear();
		for (Config config : InfMain.getConfigManager().getConfigList()) {
			configList.add(new ConfigList(config, this));
		}
		configFull = false;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Render2D.drawRectWH(matrices, x + 150, y + 1, 180, 17, 0xFF989595);
		Render2D.drawRectWH(matrices, x + 151, y + 2, 178, 15, 0xFF080808);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/game/screen/info.png"), x + 381, y + 231, 15, 15);

		if (Render2D.isHovered(mouseX, mouseY, x + 381, y + 231, 15, 15)) {
			String save = "Configs save settings, included modules and binds.";
			Render2D.drawRectWH(matrices, mouseX - 6 - FontUtils.getStringWidth(save) / 2, mouseY - 44,
					FontUtils.getStringWidth(save) + 2, 36, 0x90000000);
			FontUtils.drawHCenteredString(matrices, "This is the section for configs,", mouseX - 5, mouseY - 40, -1);
			FontUtils.drawHCenteredString(matrices, save, mouseX - 5, mouseY - 30, -1);
			FontUtils.drawHCenteredString(matrices, "Configs are limited in creation", mouseX - 5, mouseY - 20, -1);
		}

		// add button
		Render2D.drawRectWH(matrices, x + 76, y + 5, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, x + 77, y + 6, 60, 12,
				Render2D.isHovered(mouseX, mouseY, x + 76, y + 5, 62, 12) ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "add", x + 108, y + 12, -1);

		// delete button
		Render2D.drawRectWH(matrices, x + 76, y + 23, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, x + 77, y + 24, 60, 12,
				Render2D.isHovered(mouseX, mouseY, x + 76, y + 23, 62, 12) ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "delete", x + 108, y + 30, -1);

		// save button
		Render2D.drawRectWH(matrices, x + 76, y + 41, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, x + 77, y + 42, 60, 12,
				Render2D.isHovered(mouseX, mouseY, x + 76, y + 41, 62, 12) ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "save", x + 108, y + 48, -1);

		// refresh button
		Render2D.drawRectWH(matrices, x + 336, y + 5, 56, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, x + 337, y + 6, 54, 12,
				Render2D.isHovered(mouseX, mouseY, x + 336, y + 5, 56, 12) ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "refresh", x + 365, y + 12, -1);

		// load button
		Render2D.drawRectWH(matrices, x + 336, y + 23, 56, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, x + 337, y + 24, 54, 12,
				Render2D.isHovered(mouseX, mouseY, x + 336, y + 23, 56, 12) ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "load", x + 364, y + 30, -1);

		if (InfMain.getConfigManager().getConfigList().size() > 15 && configFull) {
			FontUtils.drawHVCenteredString(matrices, "Configs", x + 108, y + 65, 0xFFE24343);
			FontUtils.drawHVCenteredString(matrices, "are Full", x + 108, y + 78, 0xFFE24343);
		}

		double yOffset = 2;
		for (ConfigList configList : configList) {
			configList.setX(x + 151);
			configList.setY(yOffset + y + 20);
			configList.setWidth(178);
			configList.setHeight(13);

			yOffset += 14;

			configList.render(matrices, mouseX, mouseY);
		}
		
		textField.setX((int) (x + 151));
		textField.setY((int) (y + 2));
		textField.setWidth(179);
		textField.setHeight(20);
		textField.render(matrices, mouseX, mouseY, delta);
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x + 76, y + 5, 62, 12) && button == 0) {
			if (!(InfMain.getConfigManager().getConfigList().size() > 15)) {
				configFull = true;
				if (!textField.getText().isEmpty()) {
					if (InfMain.getConfigManager().fromName((textField.getText())) == null) {
						InfMain.getConfigManager().loadConfig(false);
						Config config = new Config(textField.getText());
						config.add();
						InfMain.getConfigManager().add(config);
						textField.setText("");
						refresh();
						Helper.infoMessage(Formatting.GRAY + "New config" + Formatting.WHITE + textField.getText() + Formatting.AQUA
								+ " added");
					}
				}
			} else {
				configFull = true;
			}
		}
		if (Render2D.isHovered(mouseX, mouseY, x + 76, y + 23, 62, 12) && button == 0) {
			for (ConfigList listButton : configList) {
				if (listButton.select) {
					InfMain.getConfigManager().delete(listButton.config);
					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.GRAY + "deleted");
				}
			}
			refresh();

		}
		if (Render2D.isHovered(mouseX, mouseY, x + 76, y + 41, 62, 12) && button == 0) {
			for (ConfigList listButton : configList) {
				if (listButton.select) {
					listButton.config.save();
					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.AQUA + "saved");
				}
			}
		}
		if (Render2D.isHovered(mouseX, mouseY, x + 336, y + 5, 56, 12) && button == 0) {
			InfMain.getConfigManager().loadConfig(false);
			refresh();
		}
		if (Render2D.isHovered(mouseX, mouseY, x + 336, y + 23, 56, 12) && button == 0) {
			for (ConfigList listButton : configList) {
				if (listButton.select) {
					listButton.config.load();

					InfMain.INSTANCE.init.menu = new ClickMenu();
					Helper.openScreen(InfMain.INSTANCE.init.menu);
					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.AQUA + "loaded");
				}
			}
		}
		if (Render2D.isHovered(mouseX, mouseY, x + 151, y + 6, 159, 15)) {
			if (button == 0)
				focused = true;
		} else {
			focused = false;
		}

		configList.forEach(configList -> configList.mouseClicked(mouseX, mouseY, button));

	}

	public String getName() {
		return name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

}
