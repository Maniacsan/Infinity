package org.infinity.ui.menu.components.config;

import org.infinity.file.config.Config;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.ClickMenu;
import org.infinity.utils.Helper;
import org.infinity.utils.StringUtil;
import org.infinity.utils.render.Render2D;
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

	private double dscale;
	private double escale;

	public ConfigButton(Config config, ConfigPanel panel) {
		this.config = config;
		this.panel = panel;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 2, height, 0xFF1F5A96);
		Render2D.drawRectWH(matrices, x + 1, y + 1, width - 2, height - 1, 0xFF171B37);

		Render2D.drawRectWH(matrices, x + width - 80, y + 6, 75, 20,
				Render2D.isHovered(mouseX, mouseY, x + width - 85, y + 6, 75, 20) ? 0xFF55B9C8 : 0xFF41A5B4);
		IFont.legacy15.drawString(matrices, "Load", x + width - 50, y + 11, -1);

		dscale = Render2D.isHovered(mouseX, mouseY, x + width - 95, y + 11, 10, 10) ? Math.min(1.2, dscale + 0.1)
				: Math.max(1, dscale - 0.1);
		escale = Render2D.isHovered(mouseX, mouseY, x + width - 110, y + 11, 10, 10) ? Math.min(1.2, escale + 0.1)
				: Math.max(1, escale - 0.1);

		matrices.push();

		double dx = x + width - 95;
		double dy = y + 11;

		matrices.translate(dx + 5, dy + 5, 0);
		matrices.scale((float) dscale, (float) dscale, 1f);
		matrices.translate(-dx - 5, -dy - 5, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/save.png"), dx, dy, 10, 10);

		matrices.pop();

		matrices.push();

		double ex = x + width - 110;
		double ey = y + 11;

		matrices.translate(ex + 5, ey + 5, 0);
		matrices.scale((float) escale, (float) escale, 1f);
		matrices.translate(-ex - 5, -ey - 5, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/delete.png"), x + width - 110,
				y + 11, 10, 10);

		matrices.pop();

		String name = StringUtil.replaceNull(config.getName());
		String author = StringUtil.replaceNull(config.getAuthor());

		if (name.length() > 38)
			name = name.substring(0, 38) + "...";

		if (author.length() > 18)
			author = author.substring(0, 18) + "...";

		IFont.legacy16.drawString(matrices, name, x + 4, y + 5, -1);
		IFont.legacy13.drawString(matrices, "Author: " + Formatting.BLUE + author, x + 5, y + 19, -1);
		IFont.legacy13.drawString(matrices, "Date: " + Formatting.BLUE + StringUtil.replaceNull(config.getDate()),
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
