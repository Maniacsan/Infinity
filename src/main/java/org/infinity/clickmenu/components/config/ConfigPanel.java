package org.infinity.clickmenu.components.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WTextField;
import org.infinity.file.config.Config;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ConfigPanel {

	public ArrayList<ConfigButton> configList = new ArrayList<>();
	public WTextField textField;
	private String name;

	// dont permission error
	private int errorTime;
	private double toasterAnim;

	private int offset;

	// calc buttons height
	private double _cbuttonHeight;

	private double x;
	private double y;
	private double width;
	private double height;

	private float refreshHover;

	public ConfigPanel(String name) {
		this.name = name;
		refresh();
	}

	public void init() {
		Helper.minecraftClient.keyboard.setRepeatEvents(true);
		textField = new WTextField(Helper.minecraftClient.textRenderer, (int) this.width / 2 - 80,
				(int) this.height / 2 - 58, 160, 22, new TranslatableText("Text"), false);

		textField.setMaxLength(45);
		textField.setColor(0xFF0B1427);
		textField.setText("New Config");
		textField.setSelected(true);
	}

	public void refresh() {
		configList.clear();
		for (Config config : InfMain.getConfigManager().getConfigList()) {
			configList.add(new ConfigButton(config, this));
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

		if (Render2D.isHovered(mouseX, mouseY, x + width - 23, y + 16, 10, 10))
			refreshHover = Math.min(350, refreshHover + 32);
		else if (refreshHover >= 0)
			refreshHover = Math.max(0, refreshHover - 32);

		Render2D.drawRectWH(matrices, x, y, width - 2, 40, 0xFF1F5A96);
		Render2D.drawRectWH(matrices, x + 1, y, width - 4, 40, 0xFF0D121D);

		Render2D.drawRectWH(matrices, x + width - 90, y + 11, 60, 18,
				Render2D.isHovered(mouseX, mouseY, x + width - 90, y + 11, 60, 18) ? 0xFFC9C9C9 : 0xFFE3E3E4);
		FontUtils.drawStringWithShadow(matrices, "Add", x + width - 60, y + 16, -1);
		RenderUtil.drawTexture(matrices, new Identifier("infinity", "/textures/icons/plus.png"), x + width - 75, y + 16,
				10, 10);

		GL11.glPushMatrix();
		double xt = x + width - 23;
		double yt = y + 16;

		GlStateManager.enableBlend();
		GL11.glTranslated(xt + 5, yt + 5, 0);
		GL11.glRotated(refreshHover, 0, 0, 1);
		GL11.glTranslated(-xt - 5, -yt - 5, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "/textures/icons/refresh.png"), xt, yt, 10, 10);
		GlStateManager.disableBlend();
		GL11.glPopMatrix();

		textField.setX((int) (x + 12));
		textField.setY((int) (y + 10));
		textField.setWidth(100);
		textField.setHeight(22);
		textField.render(matrices, mouseX, mouseY, delta);

		double yOffset = 2;

		toasterAnim = errorTime > 0 ? Math.min(3, toasterAnim + 7) : Math.max(-35, toasterAnim - 7);

		if (toasterAnim != -35) {
			Render2D.fillGradient(x + width / 2 - 70, y + toasterAnim, x + width / 2 + 70, y + toasterAnim + 35,
					0xFF171F40, 0xFF37447D);
			FontUtils.drawStringWithShadow(matrices, "Configs are limited ", x + width / 2 - 45, y + toasterAnim + 6,
					-1);
			FontUtils.drawStringWithShadow(matrices, "Buy " + Formatting.YELLOW + "Premium" + Formatting.RESET + " to unlock ", x + width / 2 - 55, y + toasterAnim + 18,
					-1);
		} 
		Render2D.startMenuScissor(x, y + 43, width - 2, height - 80);

		// scroll
		if (_cbuttonHeight > this.height - 80) {
			Render2D.drawRectWH(matrices, x + width - 5, y + 43, 2, height - 80, 0x90000000);
			Render2D.drawRectWH(matrices, x + width - 5, y + 43 + offset, 2, height - 80 - getHeightDifference(),
					0xFF1F5A96);
		}

		for (ConfigButton configButton : configList) {
			_cbuttonHeight = (int) (y + yOffset);
			configButton.setX(x + 2);
			configButton.setY(y + yOffset + 43 - offset);
			configButton.setWidth(width - 10);
			configButton.setHeight(30);

			yOffset += 35;

			configButton.render(matrices, mouseX, mouseY);
		}
		Render2D.stopScissor();
	}

	public void tick() {
		if (errorTime > 0)
			errorTime--;
		else if (errorTime < 0)
			errorTime = 0;
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x + width - 90, y + 11, 60, 18) && button == 0) {

			if (Helper.isUser() && InfMain.getConfigManager().getConfigList().size() >= 7) {
				errorTime = 60;
				return;
			}

			if (!textField.getText().isEmpty()) {
				if (InfMain.getConfigManager().fromName((textField.getText())) == null) {
					InfMain.getConfigManager().loadConfig(false);
					Config config = new Config(textField.getText(), InfMain.getUser().getName(),
							new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
					config.add();
					InfMain.getConfigManager().add(config);
					textField.setText("");
					refresh();
					Helper.infoMessage(Formatting.GRAY + "New config" + Formatting.WHITE + textField.getText()
							+ Formatting.AQUA + " added");
				}
			}
		}
		if (Render2D.isHovered(mouseX, mouseY, x + width - 23, y + 16, 10, 10) && button == 0) {
			InfMain.getConfigManager().loadConfig(false);
			refresh();
		}

		configList.forEach(configButton -> {
			if (configButton.mouseClicked(mouseX, mouseY, button)) {

			}
		});

	}

	public void mouseScrolled(double d, double e, double amount) {
		int scrollOffset = 15;
		if (_cbuttonHeight < this.height - 80)
			return;

		if (amount < 0.0D) {
			this.offset += scrollOffset;

		} else if (amount > 0.0D) {
			this.offset -= scrollOffset;
		}

		if (_cbuttonHeight > this.height - 80) {
			int difference = getHeightDifference();
			if (offset > difference)
				offset = difference;
			else if (offset < 0)
				offset = 0;
		}
	}
	
	public void onClose() {
		errorTime = 0;
	}

	public int getHeightDifference() {
		double diffHeight = this.height - 80;
		return (int) (this.getListHeight() - diffHeight);
	}

	private int getListHeight() {
		int height = 0;
		for (ConfigButton configButton : configList)
			height += (configButton.getHeight() + 5);
		return height;
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