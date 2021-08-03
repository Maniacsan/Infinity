package org.infinity.ui.menu.components.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.infinity.file.config.Config;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.util.FontUtils;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.ui.menu.widgets.WTextField;
import org.infinity.utils.Helper;
import org.infinity.utils.Timer;
import org.infinity.utils.render.RenderUtil;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class ConfigPanel {

	public ArrayList<ConfigButton> configList = new ArrayList<>();
	public WTextField textField;
	private String name;

	private Timer scrollTimer = new Timer();

	// dont permission error
	private int errorTime = -1;
	private double toasterAnim;

	public float fade;

	private double scrollSpeed;
	private double prevScrollProgress;
	private double scrollProgress;
	private double _cbuttonHeight;

	private double x;
	private double y;
	private double width;
	private double height;

	private float refreshHover;

	public ConfigPanel(String name) {
		this.name = name;
	}

	public void init() {
		Helper.MC.keyboard.setRepeatEvents(true);
		textField = new WTextField(0xFF0B1427, false);

		textField.setMaxLength(45);
		textField.setText("New Config");
		refresh();
	}

	public void refresh() {
		configList.clear();
		for (Config config : InfMain.getConfigManager().getConfigList()) {
			configList.add(new ConfigButton(config, this));
		}
		refreshHover = 0;
		errorTime = -1;
		setScrollProgress(0);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

		if (refreshHover != 360)
			refreshHover = Math.min(360, refreshHover + 37);

		Render2D.drawRectWH(matrices, x, y, width - 2, 40, 0xFF1F5A96);
		Render2D.drawRectWH(matrices, x + 1, y, width - 4, 40, 0xFF0D121D);

		Render2D.drawRectWH(matrices, x + width - 90, y + 11, 60, 18,
				Render2D.isHovered(mouseX, mouseY, x + width - 90, y + 11, 60, 18) ? 0xFFC9C9C9 : 0xFFE3E3E4);
		IFont.legacy15.drawStringWithShadow(matrices, "Add", x + width - 67, y + 15, -1);

		matrices.push();
		double xt = x + width - 23;
		double yt = y + 16;

		GlStateManager._enableBlend();
		matrices.translate(xt + 5, yt + 5, 0);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) refreshHover));
		matrices.translate(-xt - 5, -yt - 5, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "/textures/icons/refresh.png"), xt, yt, 10, 10);
		GlStateManager._disableBlend();
		matrices.pop();

		textField.setX((int) (x + 12));
		textField.setY((int) (y + 10));
		textField.setWidth(100);
		textField.setHeight(22);
		textField.render(matrices, mouseX, mouseY, delta);

		double yOffset = 2;

		toasterAnim = errorTime > 0 ? Math.min(3, toasterAnim + 7)
				: errorTime == -1 ? -35 : Math.max(-35, toasterAnim - 7);

		if (toasterAnim != -35) {
			Render2D.verticalGradient(matrices, x + width / 2 - 70, y + toasterAnim, x + width / 2 + 70,
					y + toasterAnim + 35, 0xFF171F40, 0xFF37447D);
			FontUtils.drawStringWithShadow(matrices, "Configs are limited ", x + width / 2 - 45, y + toasterAnim + 6,
					-1);
			FontUtils.drawStringWithShadow(matrices,
					"Buy " + Formatting.YELLOW + "Premium" + Formatting.RESET + " to unlock ", x + width / 2 - 55,
					y + toasterAnim + 18, -1);
		}
		Render2D.startScissor(x, y + 43, width - 2, height - 80);

		// scroll
		if (_cbuttonHeight > this.height - 80) {
			Render2D.drawRectWH(matrices, x + width - 5, y + 43, 2, height - 80, 0x90000000);
			Render2D.drawRectWH(matrices, x + width - 5, y + 43 + getScrollProgress(), 2,
					height - 80 - getHeightDifference(), 0xFF1F5A96);
		}

		for (ConfigButton configButton : configList) {
			_cbuttonHeight = (int) (y + yOffset);
			configButton.setX(x + 2);
			configButton.setY(y + yOffset + 43 - getScrollProgress());
			configButton.setWidth(width - 10);
			configButton.setHeight(30);

			yOffset += 35;

			RenderSystem.setShaderColor(1f, 1f, 1f, fade);
			configButton.render(matrices, mouseX, mouseY);
		}
		Render2D.stopScissor();
	}

	public void tick() {
		if (errorTime > 0)
			errorTime--;

		if (_cbuttonHeight < this.height - 80)
			return;

		int difference = getHeightDifference();

		setScrollProgress(scrollProgress + scrollSpeed);
		scrollSpeed *= 0.54;

		if (scrollTimer.hasReached(100)) {
			if (scrollProgress < 0)
				scrollSpeed = scrollProgress * -0.35;
			else if (scrollProgress > difference)
				scrollSpeed = (scrollProgress - difference) * -0.35;
		}
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

		textField.mouseClicked(mouseX, mouseY, button);

	}

	public void mouseScrolled(double d, double e, double amount) {
		if (amount != 0) {
			double sa = amount < 0 ? amount - 10 : amount + 10;
			scrollTimer.reset();
			scrollSpeed -= sa;
		}
	}

	public void charTyped(char chr, int keyCode) {
		textField.charTyped(chr, keyCode);
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		textField.keyPressed(keyCode, scanCode, modifiers);
	}

	public void onClose() {
		errorTime = 0;
		fade = -0.3f;
		textField.onClose();
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

	private double getScrollProgress() {
		return prevScrollProgress
				+ (scrollProgress - prevScrollProgress) * Helper.MC.getLastFrameDuration();
	}

	private void setScrollProgress(double value) {
		prevScrollProgress = scrollProgress;
		scrollProgress = value;
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
