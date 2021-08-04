package org.infinity.ui.util;

import java.awt.Color;

import org.infinity.font.IFont;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CustomFieldWidget {
	/**
	 * Stores text for the fill in
	 */
	private String fillText = "";

	/**
	 * Stores the current text in the text field
	 */
	private String text = "";

	private boolean focused = false;

	private int maxLength;

	private int color;

	private double x;
	private double y;
	private double width;
	private double height;

	private Identifier image;
	private int imageColor;

	private boolean obftext;

	private double wanim;

	public CustomFieldWidget(Identifier image, int color, boolean obftext) {
		this.text = "";
		this.maxLength = 32;
		this.focused = true;
		this.image = image;
		this.obftext = obftext;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		String text = obftext ? getText().replaceAll("(?s).", "*") : getText();

		double diff = Math.max(IFont.legacy15.getStringWidth(text) + 4 - getWidth(), 0);
		wanim = focused ? RenderUtil.animate(width, wanim, 0.4) : RenderUtil.animate(0, wanim, 0.4);

		if (image != null) {
			RenderUtil.drawTexture(matrices, image, x - 16, y + height / 2 - 5, 12, 12);
		}

		Render2D.drawRectWH(matrices, x, y + height - 1, width, 0.5, 0xFF767E8F);
		Render2D.horizontalGradient(matrices, x, y + height - 1, x + wanim, (y + height - 1) + 1, 0xFF92A7D4,
				0xFF335AAE);

		matrices.push();
		Render2D.startScissor(x, y, width, height);
		if (!text.isEmpty()) {
			IFont.legacy15.drawString(matrices, text, getX() + 2 - diff,
					getY() + (getHeight() - IFont.legacy14.getFontHeight()) / 2, -1);
		}
		Render2D.stopScissor();
		matrices.pop();

		if (!focused && text.isEmpty() && !fillText.isEmpty())
			IFont.legacy15.drawString(matrices, fillText, getX() + 2,
					getY() + (getHeight() - IFont.legacy15.getFontHeight()) / 2, 0x80F9F9F9);

		if (focused) {
			Render2D.drawRectWH(matrices, getX() + IFont.legacy15.getStringWidth(text) + 4 - diff,
					getY() + (getHeight() + 5) / 2, 4, 1,
					new Color(255, 255, 255, System.currentTimeMillis() % 2000 > 1000 ? 255 : 0).getRGB());
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (Render2D.isHovered(mouseX, mouseY, x, y, width, height) && mouseButton == 0)
			focused = true;
		else
			focused = false;
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (focused) {
			if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
				if (getText().length() != 0)
					setText(getText().substring(0, getText().length() - 1));
			} else if (Helper.isPaste(keyCode)) {
				setText(getText() + Helper.MC.keyboard.getClipboard());
			}
		}
	}

	public void charTyped(Character typedChar, int keyCode) {
		if (focused) {
			if (typedChar != null && text.length() < maxLength)
				setText(getText() + typedChar);
		}
	}

	public void onClose() {
		focused = false;
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		text = value;
	}

	public void setFillText(String value) {
		fillText = value;
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

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean isFocused() {
		return focused;
	}

	public int getImageColor() {
		return imageColor;
	}

	public void setImageColor(int imageColor) {
		this.imageColor = imageColor;
	}

	public boolean isObfedText() {
		return obftext;
	}

	public void setObfedText(boolean state) {
		this.obftext = state;
	}

}