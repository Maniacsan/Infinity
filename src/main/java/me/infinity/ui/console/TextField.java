package me.infinity.ui.console;

import org.lwjgl.glfw.GLFW;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import net.minecraft.client.util.math.MatrixStack;

public class TextField {

	private double x;
	private double y;
	private double width;
	private String text = "";

	public TextField(double x, double y, double width) {
		this.x = x;
		this.y = y;
		this.width = width;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y) {
		if (FontUtils.getStringWidth(this.text) < 265)
		Render2D.drawRectWH(matrices, x + FontUtils.getStringWidth(text) + 1, y + 7, 4, 1, -1);
		FontUtils.drawStringWithShadow(matrices, text, x, y, 0xFFFFFFFF);
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
			if (this.text.length() > 1) {
				this.text = this.text.substring(0, this.text.length() - 1);
			} else if (this.text.length() == 1) {
				this.text = "";
			}
		}
	}

	public void charTyped(char chr, int keyCode) {
		String specialChars = "/*@#$%^&*;";
		if ((Character.isLetterOrDigit(chr) || Character.isSpaceChar(chr)
				|| specialChars.contains(Character.toString(chr)))
				&& FontUtils.getStringWidth(this.text) < 265) {
			this.text += Character.toString(chr);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
