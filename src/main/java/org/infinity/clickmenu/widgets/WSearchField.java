package org.infinity.clickmenu.widgets;

import java.awt.Color;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WSearchField {

	/**
	 * Stores text for the fill in
	 */
	private String fillText = "";

	/**
	 * Stores the current text in the text field
	 */
	private String text = "";

	/**
	 * Stores whether the cursor selected the text field and should listen for
	 * keystrokes
	 */
	private boolean focused = false;

	private int maxLength;

	/**
	 * Stores the color of the text field
	 */
	private int color;

	private double x;
	private double y;
	private double width;
	private double height;

	private Panel panel;

	private int position;

	public WSearchField(Panel panel) {
		this.panel = panel;
		this.color = 0xFFA5A6A6;
		this.text = "";
		maxLength = 80;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		position = (int) (panel.isOpenSearch() ? RenderUtil.animate(0, position, 0.4)
				: RenderUtil.animate(width - 6, position, 0.4));

		if (position < width - 8) {
			Render2D.drawRect(matrices, x + 1 + position - 13, y, x + width, y + height, color);
		}

		RenderUtil.drawImage(matrices, true, x - 9 + position,
				y + 3, 10, 10, "/assets/infinity/textures/icons/search.png");

		if (!panel.isOpenSearch())
			return;

		if (!focused && getText().isEmpty())
			IFont.legacy15.drawString(matrices, "search..", x + 5,
					getY() + (getHeight() - IFont.legacy15.getFontHeight()) / 2 + 1, 0xFF979797);

		if (position != 0)
			return;

		double diff = Math.max(IFont.legacy15.getStringWidth(getText()) + 6 - getWidth(), 0);

		Render2D.startScissor(x + 4, y, width - 4, height);
		if (!getText().isEmpty()) {
			IFont.legacy15.drawString(matrices, getText(), getX() + 4 - diff,
					getY() + (getHeight() - IFont.legacy15.getFontHeight()) / 2 + 1, -1);
		}

		if (!focused && text.isEmpty() && !fillText.isEmpty())
			IFont.legacy15.drawString(matrices, fillText, getX() + 4,
					getY() + (getHeight() - IFont.legacy15.getFontHeight()) + 1, -1);

		Render2D.stopScissor();

		if (focused) {
			Render2D.drawRectWH(matrices, getX() + IFont.legacy15.getStringWidth(getText()) + 6 - diff,
					getY() + (getHeight() + 4) / 2 + 1, 4, 1,
					new Color(255, 255, 255, System.currentTimeMillis() % 2000 > 1000 ? 255 : 0).getRGB());
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (Render2D.isHovered(mouseX, mouseY, x, y, width, height) && mouseButton == 0)
			focused = true;
		else
			focused = false;

		boolean openHover = mouseX >= (double) this.x - 13 + position && mouseX < (double) (this.x + 6 + position)
				&& mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
		if (openHover && mouseButton == 0)
			panel.setOpenSearch(!panel.isOpenSearch());
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (focused) {
			if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
				if (getText().length() != 0)
					setText(getText().substring(0, getText().length() - 1));
			} else if (Helper.isPaste(keyCode)) {
				setText(getText() + Helper.minecraftClient.keyboard.getClipboard());
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

	/**
	 * Setter for text in field
	 *
	 * @param value text
	 */
	public void setText(String value) {
		text = value;
	}

	/**
	 * Sets the fill text
	 *
	 * @param value text
	 */
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

	public void setFocused(boolean focused) {
		this.focused = focused;
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

}