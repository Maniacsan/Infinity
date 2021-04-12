package me.infinity.clickmenu.config;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import me.infinity.InfMain;
import me.infinity.clickmenu.Panel;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.file.config.Config;
import me.infinity.utils.Helper;
import me.infinity.utils.render.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ConfigButton {

	public ArrayList<ConfigListButton> configList = new ArrayList<>();
	private final Identifier INFO = new Identifier("infinity", "info.png");
	protected String content = "";
	protected boolean focused;
	private boolean addHovered;
	private boolean deleteHovered;
	private boolean saveHovered;
	private boolean textHovered;
	private boolean refreshHovered;
	private boolean loadHovered;
	private boolean infoHovered;
	private boolean configFull;
	public boolean add;
	public boolean load;
	public boolean delete;
	public boolean save;
	private double width;

	public ConfigButton(Panel panel) {
		refresh();
		configFull = false;
	}

	public void refresh() {
		configList.clear();
		for (Config config : InfMain.getConfigManager().getConfigList())
			configList.add(new ConfigListButton(config, this));
		configFull = false;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height, double yMod, double setX, double setY, double setWidth, double setHeight) {
		this.width = width;

		//
		this.textHovered = Render2D.isHovered(mouseX, mouseY, setX + 151, setY + 6, 159, 15);
		this.addHovered = Render2D.isHovered(mouseX, mouseY, setX + 76, setY + 5, 62, 12);
		this.deleteHovered = Render2D.isHovered(mouseX, mouseY, setX + 76, setY + 23, 62, 12);
		this.saveHovered = Render2D.isHovered(mouseX, mouseY, setX + 76, setY + 41, 62, 12);
		this.loadHovered = Render2D.isHovered(mouseX, mouseY, setX + 316, setY + 23, 56, 12);
		this.refreshHovered = Render2D.isHovered(mouseX, mouseY, setX + 316, setY + 5, 56, 12);
		this.infoHovered = Render2D.isHovered(mouseX, mouseY, setX + 381, setY + 231, 15, 15);

		Render2D.drawRectWH(matrices, setX + 150, setY + 1, 180, 17, 0xFF989595);
		Render2D.drawRectWH(matrices, setX + 151, setY + 2, 178, 15, 0xFF080808);
		FontUtils.drawStringWithShadow(matrices, content, setX + 154, setY + 6, -1);

		if (focused && FontUtils.getStringWidth(content) < 170) {
			Render2D.drawRectWH(matrices, setX + 153 + FontUtils.getStringWidth(content) + 2, setY + 13, 3, 1, -1);
		}

		RenderUtil.drawTexture(matrices, INFO, setX + 381, setY + 231, 15, 15);

		if (infoHovered) {
			String save = "Configs save settings, included modules and binds.";
			Render2D.drawRectWH(matrices, mouseX - 6 - FontUtils.getStringWidth(save) / 2, mouseY - 44,
					FontUtils.getStringWidth(save) + 2, 36, 0x90000000);
			FontUtils.drawHCenteredString(matrices, "This is the section for configs,", mouseX - 5, mouseY - 40, -1);
			FontUtils.drawHCenteredString(matrices, save, mouseX - 5, mouseY - 30, -1);
			FontUtils.drawHCenteredString(matrices, "Configs are limited in creation", mouseX - 5, mouseY - 20, -1);
		}

		// add button
		Render2D.drawRectWH(matrices, setX + 76, setY + 5, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, setX + 77, setY + 6, 60, 12, addHovered ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "add", setX + 108, setY + 12, -1);

		// delete button
		Render2D.drawRectWH(matrices, setX + 76, setY + 23, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, setX + 77, setY + 24, 60, 12, deleteHovered ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "delete", setX + 108, setY + 30, -1);

		// save button
		Render2D.drawRectWH(matrices, setX + 76, setY + 41, 62, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, setX + 77, setY + 42, 60, 12, saveHovered ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "save", setX + 108, setY + 48, -1);

		// refresh button
		Render2D.drawRectWH(matrices, setX + 336, setY + 5, 56, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, setX + 337, setY + 6, 54, 12, refreshHovered ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "refresh", setX + 365, setY + 12, -1);

		// load button
		Render2D.drawRectWH(matrices, setX + 336, setY + 23, 56, 14, 0xFF8E8E8E);
		Render2D.drawRectWH(matrices, setX + 337, setY + 24, 54, 12, loadHovered ? 0x903B3B3B : 0xFF131313);
		FontUtils.drawHVCenteredString(matrices, "load", setX + 364, setY + 30, -1);

		if (InfMain.getConfigManager().getConfigList().size() > 15 && configFull) {
			FontUtils.drawHVCenteredString(matrices, "Configs", setX + 108, setY + 65, 0xFFE24343);
			FontUtils.drawHVCenteredString(matrices, "are Full", setX + 108, setY + 78, 0xFFE24343);
		}

		double yOffset = 2;
		for (ConfigListButton configList : configList) {
			configList.render(matrices, mouseX, mouseY, setX + 151, yOffset + setY + 20, 178, 13);
			yOffset += 14;
		}

	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.addHovered && button == 0) {
			if (!(InfMain.getConfigManager().getConfigList().size() > 15)) {
				configFull = true;
				if (!content.isEmpty()) {
					if (InfMain.getConfigManager().fromName((content)) == null) {
						InfMain.getConfigManager().loadConfig(false);
						Config config = new Config(content);
						config.add();
						InfMain.getConfigManager().add(config);
						content = "";
						refresh();
						Helper.infoMessage(Formatting.GRAY + "New config" + Formatting.WHITE + content + Formatting.AQUA
								+ " added");
					}
				}
			} else {
				configFull = true;
			}
		} else if (this.deleteHovered && button == 0) {
			for (ConfigListButton listButton : configList) {
				if (listButton.select) {
					InfMain.getConfigManager().delete(listButton.config);
					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.GRAY + "deleted");
				}
			}
			refresh();

		} else if (this.saveHovered && button == 0) {
			for (ConfigListButton listButton : configList) {
				if (listButton.select) {
					listButton.config.save();
					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.AQUA + "saved");
				}
			}
		} else if (this.refreshHovered && button == 0) {
			InfMain.getConfigManager().loadConfig(false);
			refresh();
		} else if (this.loadHovered && button == 0) {
			for (ConfigListButton listButton : configList) {
				if (listButton.select) {
					listButton.config.load();

					Helper.infoMessage(listButton.config.getName() + " config " + Formatting.AQUA + "loaded");
				}
			}
		}
		if (this.textHovered) {
			if (button == 0)
				focused = true;
		} else {
			focused = false;
		}

		configList.forEach(configList -> configList.mouseClicked(mouseX, mouseY, button));

	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
			if (this.content.length() > 1) {
				this.content = this.content.substring(0, this.content.length() - 1);
			} else if (this.content.length() == 1) {
				this.content = "";
			}
		}
	}

	public void charTyped(char chr, int keyCode) {
		if (this.focused) {
			String specialChars = "/*!@#$%^&*()\";:'{}_[]|\\?/<>,.";
			if ((Character.isLetterOrDigit(chr) || Character.isSpaceChar(chr)
					|| specialChars.contains(Character.toString(chr)))
					&& FontUtils.getStringWidth(this.content) < this.width + 110.0D) {
				this.content += Character.toString(chr);
			}
		}
	}

}
