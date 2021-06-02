package org.infinity.clickmenu.components.config;

import org.infinity.clickmenu.ClickMenu;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.file.config.Config;
import org.infinity.main.InfMain;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.StringUtil;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ConfigButton {

	private ConfigPanel panel;
	private Config config;

	private double x;
	private double y;
	private double width;
	private double height;

	public ConfigButton(Config config, ConfigPanel panel) {
		this.config = config;
		this.panel = panel;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 2, height, 0xFF1F5A96);
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 2, height - 1, 0xFF171B37);

		Render2D.drawRectWH(matrices, x + width - 80, y + 6, 75, 20,
				Render2D.isHovered(mouseX, mouseY, x + width - 85, y + 6, 75, 20) ? 0xFF55B9C8 : 0xFF41A5B4);
		IFont.legacy15.drawString("Load", x + width - 50, y + 11, -1);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/save.png"), x + width - 95, y + 11,
				10, 10);
		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/delete.png"), x + width - 110,
				y + 11, 10, 10);

		String name = StringUtil.replaceNull(config.getName());
		String author = StringUtil.replaceNull(config.getAuthor());

		if (name.length() > 38)
			name = name.substring(0, 38) + "...";
		
		if (author.length() > 20)
			author = author.substring(0, 20) + "...";

		IFont.legacy16.drawString(name, x + 4, y + 5, -1);
		IFont.legacy13.drawString("Author: " + author, x + 5, y + 19, -1);
		IFont.legacy13.drawString("Date: " + StringUtil.replaceNull(config.getDate()),
				x + IFont.legacy13.getStringWidth("Author: " + author) + 9, y + 19, -1);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0)
			return false;

		if (Render2D.isHovered(mouseX, mouseY, x + width - 85, y + 6, 75, 20)) {
			config.load();

			InfMain.INSTANCE.init.menu = new ClickMenu();
			Helper.openScreen(InfMain.INSTANCE.init.menu);
			Helper.infoMessage(config.getName() + " config " + Formatting.BLUE + "loaded");
			return true;
		}

		if (Render2D.isHovered(mouseX, mouseY, x + width - 95, y + 11, 10, 10)) {
			config.save();
			Helper.infoMessage(config.getName() + " config " + Formatting.BLUE + "saved");
			return true;
		}

		if (Render2D.isHovered(mouseX, mouseY, x + width - 110, y + 11, 10, 10)) {
			InfMain.getConfigManager().delete(config);
			Helper.infoMessage(config.getName() + " config " + Formatting.BLUE + "deleted");
			return true;
		}
		return false;
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

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
