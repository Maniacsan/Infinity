package org.infinity.protect.ui;

import java.awt.Color;

import org.infinity.InfMain;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.mixin.IAbstractButtonWidget;
import org.infinity.ui.util.CustomButtonWidget;
import org.infinity.ui.util.CustomFieldWidget;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

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
	private int errorTime;

	public AuthUI() {
		super(new LiteralText(""));
	}

	@Override
	public void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.usernameField = new CustomFieldWidget(this.textRenderer, this.width / 2 - 80, this.height / 2 - 58, 160,
				22, new TranslatableText("Login"), false);
		this.usernameField.setMaxLength(40);
		this.children.add(usernameField);
		this.passwordField = new CustomFieldWidget(this.textRenderer, this.width / 2 - 80, this.height / 2 - 26, 160,
				22, new TranslatableText("Password"), true);
		this.passwordField.setMaxLength(128);
		this.children.add(passwordField);

		this.loginButton = (CustomButtonWidget) this.addButton(new CustomButtonWidget(this.width / 2 - 50,
				this.height / 2 + 15, 100, 20, new TranslatableText("Login"), (buttonWidget) -> {
					if (!usernameField.getText().isEmpty()) {
						new Thread(() -> {
							Protect.LOGIN.login(usernameField.getText(), passwordField.getText());

							if (Protect.LOGIN.getAuth().getType().equals(AuthType.SUCCESS)) {
								// TODO Action
							} else {
								errorTime = 45;
							}
						}).start();
					}
				}));
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean exitHover = Render2D.isFillHovered(mouseX, mouseY, this.width / 2 + 101, height / 2 - 146,
				this.width / 2 + 101 + 18.5, this.height / 2 - 146 + 13.5);
		boolean hovered = Render2D.isFillHovered(mouseX, mouseY, this.width / 2 + 76, height / 2 - 132,
				this.width / 2 + 76 + 50, this.height / 2 - 115);

		if (hovered && button == 0) {
			Helper.openSite("https://whyuleet.ru/community/entry/register");
		}
		if (exitHover && button == 0) {
			Helper.minecraftClient.stop();
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		loginButton.active = !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty();

		if (usernameField.isFocused())
			((IAbstractButtonWidget) passwordField).setCustomFocused(false);

		if (errorTime > 0) {
			errorTime--;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		boolean exitHover = Render2D.isFillHovered(mouseX, mouseY, this.width / 2 + 101, height / 2 - 146,
				this.width / 2 + 101 + 18.5, this.height / 2 - 146 + 13.5);
		boolean hovered = Render2D.isFillHovered(mouseX, mouseY, this.width / 2 + 76, height / 2 - 132,
				this.width / 2 + 76 + 50, this.height / 2 - 115);
		Render2D.drawRectWH(matrices, 0, 0, width, height, 0xFF212D59);

		Render2D.drawRectWH(matrices, width / 2 - 122, height / 2 - 131, 244, 263, 0xFF202020);
		Render2D.drawRectWH(matrices, width / 2 - 120, height / 2 - 130, 240, 260, 0xFFD4DADA);

		Render2D.drawRectWH(matrices, width / 2 - 121, height / 2 - 146, 242, 15, 0xFF6B7487);

		Render2D.drawRectWH(matrices, width / 2 + 101, height / 2 - 146, 18.5, 13.5,
				exitHover ? 0xFFF86155 : 0xFFFFFFFF);

		FontUtils.drawString(matrices, "x", this.width / 2 + 107, this.height / 2 - 144,
				exitHover ? 0xFFFFFFFF : 0xFF000000);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "logo.png"), width / 2 - 60 / 2,
				this.height / 2 - 126, 60, 60);

		this.usernameField.render(matrices, mouseX, mouseY, delta);
		this.passwordField.render(matrices, mouseX, mouseY, delta);

		if (!usernameField.isActive() && usernameField.getText().isEmpty())
			drawStringWithShadow(matrices, this.textRenderer, "Email or Username", this.width / 2 - 77,
					this.height / 2 - 51, 0x60526880);

		if (!passwordField.isActive() && passwordField.getText().isEmpty())
			drawStringWithShadow(matrices, this.textRenderer, "********", this.width / 2 - 77, this.height / 2 - 17,
					0x60526880);

		if (errorTime > 0) {
			Render2D.drawRectWH(matrices, this.width / 2 - 51, this.height / 2 - 126, 102, 30, -1);
			Render2D.drawRectWH(matrices, this.width / 2 - 50, this.height / 2 - 125, 100, 28,
					new Color(181, 59, 86).getRGB());
			FontUtils.drawHCenteredString(matrices, "Login failed", this.width / 2, this.height / 2 - 115, -1);
		}

		drawStringWithShadow(matrices, this.textRenderer, "Register", this.width / 2 + 77, this.height / 2 - 127,
				hovered ? 0xFFEDFAF9 : 0xFFAFADAD);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		if (!Protect.CHECK.getResult().get().equalsIgnoreCase("true")) {
			Helper.openScreen(InfMain.authUI);
		}
	}
}
