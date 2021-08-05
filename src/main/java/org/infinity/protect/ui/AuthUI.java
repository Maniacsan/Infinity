package org.infinity.protect.ui;

import java.awt.Color;

import org.infinity.file.AuthInfo;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.ui.util.CustomButtonWidget;
import org.infinity.ui.util.CustomFieldWidget;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;

import me.protect.Protect;
import me.protect.connection.Auth.AuthType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class AuthUI extends Screen {

	private CustomFieldWidget usernameField;
	private CustomFieldWidget passwordField;
	private CustomButtonWidget loginButton;
	private String usernameData;
	private String passwordData;

	private boolean hovered;
	private double shadowX;
	private double shadowY;
	private double xscale;
	private int errorTime;

	public AuthUI() {
		super(new LiteralText(""));
		String[] data = AuthInfo.INSTANCE.loadAccount();
		usernameData = data[0];
		passwordData = data[1];
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		usernameField = new CustomFieldWidget(new Identifier("infinity", "textures/icons/auth/username.png"), -1,
				false);
		if (usernameData != null && !usernameData.isEmpty())
			usernameField.setText(usernameData);

		usernameField.setMaxLength(40);
		usernameField.setFocused(false);

		passwordField = new CustomFieldWidget(new Identifier("infinity", "textures/icons/auth/password.png"), -1, true);
		if (passwordData != null && !passwordData.isEmpty())
			passwordField.setText(passwordData);

		passwordField.setMaxLength(128);
		passwordField.setFocused(false);

		loginButton = (CustomButtonWidget) this.addDrawableChild(new CustomButtonWidget(this.width / 2 - 40,
				this.height / 2 + 25, 80, 20, new TranslatableText("Login"), (buttonWidget) -> {
					if (!usernameField.getText().isEmpty()) {
						login();
					}
				}));
		shadowY = 70;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		usernameField.mouseClicked(mouseX, mouseY, button);
		passwordField.mouseClicked(mouseX, mouseY, button);

		if (Render2D.isHovered(mouseX, mouseY, width / 2 - 95, height / 2 + 70, 16, 16)) {
			Helper.openSite("https://vk.com/whyuleet");
		}

		if (Render2D.isHovered(mouseX, mouseY, width / 2 + 65, height / 2 + 75,
				IFont.legacy13.getStringWidth("Register") + 2, IFont.legacy13.getFontHeight() + 2) && button == 0) {
			Helper.openSite("https://whyuleet.ru/community/entry/register");
		}
		if (Render2D.isHovered(mouseX, mouseY, width / 2 + 85, height / 2 - 95, 12, 12) && button == 0) {
			Helper.MC.stop();
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		loginButton.active = !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty();

		if (errorTime > 0) {
			errorTime--;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		hovered = Render2D.isHovered(mouseX, mouseY, width / 2 - 100, height / 2 - 100, 200, 190);

		boolean exitHover = Render2D.isHovered(mouseX, mouseY, width / 2 + 85, height / 2 - 95, 12, 12);
		boolean rhovered = Render2D.isHovered(mouseX, mouseY, width / 2 + 65, height / 2 + 75,
				IFont.legacy13.getStringWidth("Register") + 2, IFont.legacy13.getFontHeight() + 2);

		Render2D.verticalGradient(matrices, 0, 0, width, height, 0xFF0B0D1B, 0xFF243E76);

		shadowY = hovered ? Math.min(80, shadowY + 7) : Math.max(65, shadowY - 7);
		shadowX = hovered ? Math.min(87, shadowX + 7) : Math.max(75, shadowX - 7);

		Render2D.verticalGradient(matrices, width / 2 - 100, height / 2 + shadowY, (width / 2 + 100), height / 2 + 94,
				0xFF000000, new Color(0, 0, 0, 0).getRGB());
		Render2D.horizontalGradient(matrices, width / 2 + shadowX, height / 2 - 100, width / 2 + 104, height / 2 + 90,
				0xFF000000, new Color(0, 0, 0, 0).getRGB());

		Render2D.drawRectWH(matrices, width / 2 - 100, height / 2 - 100, 200, 190, 0xFF243761);

		usernameField.setX(width / 2 - 60);
		usernameField.setY(height / 2 - 45);
		usernameField.setWidth(130);
		usernameField.setHeight(20);
		usernameField.setFillText("Username");

		usernameField.render(matrices, mouseX, mouseY, delta);

		passwordField.setX(width / 2 - 60);
		passwordField.setY(height / 2 - 16);
		passwordField.setWidth(130);
		passwordField.setHeight(20);
		passwordField.setFillText("********");

		passwordField.render(matrices, mouseX, mouseY, delta);

		if (errorTime > 0) {
			Render2D.drawRectWH(matrices, this.width / 2 - 51, this.height / 2 - 126, 102, 30, -1);
			Render2D.drawRectWH(matrices, this.width / 2 - 50, this.height / 2 - 125, 100, 28,
					new Color(181, 59, 86).getRGB());
			IFont.legacy16.drawCenteredString(matrices, "Login failed", this.width / 2, this.height / 2 - 115, -1);
		}

		IFont.legacy13.drawString(matrices, "Register", this.width / 2 + 66, this.height / 2 + 76,
				rhovered ? 0xFFEDFAF9 : 0xFF6B6A6A);

		xscale = exitHover ? Math.min(1.2, xscale + 0.1) : 1;

		matrices.push();
		double dx = width / 2 + 86;
		double dy = (height / 2) - 94;

		matrices.translate(dx + 4, dy + 4, 0);
		matrices.scale((float) xscale, (float) xscale, 1);
		matrices.translate(-dx - 4, -dy - 4, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/exit.png"), dx, dy, 8, 8);
		matrices.pop();

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/auth/vk-64.png"), width / 2 - 95,
				height / 2 + 70, 16, 16);

		String ip = Render2D.isHovered(mouseX, mouseY, 1,
				height - (IFont.legacy13.getFontHeight() + IFont.legacy13.getFontHeight()) - 2,
				IFont.legacy13.getStringWidth("Your IP: " + Protect.IP), IFont.legacy13.getFontHeight()) ? Protect.IP
						: "***.***.***.***.**";

		IFont.legacy13.drawString(matrices, "Your IP: " + ip, 1,
				height - (IFont.legacy13.getFontHeight() + IFont.legacy13.getFontHeight()) - 2, -1);
		IFont.legacy13.drawString(matrices, "Your HWID: " + Protect.HWID.getHWID(), 1,
				height - IFont.legacy13.getFontHeight() - 2, -1);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		usernameField.keyPressed(keyCode, scanCode, modifiers);
		passwordField.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == GLFW.GLFW_KEY_ENTER && loginButton.active) {
			login();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		usernameField.charTyped(typedChar, keyCode);
		passwordField.charTyped(typedChar, keyCode);
		return super.charTyped(typedChar, keyCode);
	}

	private void login() {
		if (Protect.LOGIN.login(usernameField.getText(), passwordField.getText()) != null) {
			if (!Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {
				errorTime = 45;
			} else
				AuthInfo.INSTANCE.saveAccount(Protect.LOGIN.getAuth().getUsername(),
						Protect.LOGIN.getAuth().getPassword());
		}
	}

	@Override
	public void onClose() {
		usernameField.onClose();
		passwordField.onClose();
		if (!Protect.CHECK.getResult().get().equalsIgnoreCase("true")) {
			Helper.openScreen(InfMain.INSTANCE.init.authUI);
		}
	}
}
