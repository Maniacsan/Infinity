package org.infinity.ui.menu.components.elements;

import java.awt.Color;

import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.math.MatrixStack;

public class TextFieldElement extends AbstractElement {

	private boolean focused = false;

	private String fillText = "";
	private String text = "";

	private int maxLength;
	private double anim;

	public TextFieldElement(Setting setting) {
		super(setting);
	}

	@Override
	public void init() {
		if (setting.getText() != null)
		setText(setting.getText());
		
		maxLength = 35;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		double diff = Math.max(IFont.legacy14.getStringWidth(getText()) + 4 - getWidth(), 0);

		if (!setting.getText().equalsIgnoreCase(text))
			setting.setText(text);

		IFont.legacy12.drawString(matrices, setting.getName() + ":", x, y + 2, 0xFFC1C1C1);
		Render2D.drawRectWH(matrices, x, y + height + 5, width, 0.5, 0xFF767E8F);

		anim = focused ? RenderUtil.animate(width, anim, 0.4) : RenderUtil.animate(0, anim, 0.4);

		Render2D.horizontalGradient(matrices, x, y + height + 5, x + anim, (y + height + 5) + 1, 0xFF92A7D4,
				0xFF335AAE);

		if (!getText().isEmpty()) {
			IFont.legacy14.drawString(matrices, getText(), x - diff, getY() + (getHeight() - 6), -1);
		}

		if (!focused && text.isEmpty() && !fillText.isEmpty())
			IFont.legacy14.drawString(matrices, fillText, x,
					getY() + (getHeight() - IFont.legacy14.getFontHeight()) / 2, -1);

		if (focused) {
			Render2D.drawRectWH(matrices, x + IFont.legacy14.getStringWidth(getText()) + 3 - diff,
					getY() + (getHeight() + 1), 4, 1,
					new Color(255, 255, 255, System.currentTimeMillis() % 2000 > 1000 ? 255 : 0).getRGB());
		}

	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x, y, width, height + 5) && button == 0)
			focused = true;
		else
			focused = false;
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseScrolled(double d, double e, double amount) {
		// TODO Auto-generated method stub

	}

	@Override
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

	@Override
	public void onClose() {
		focused = false;
	}

	@Override
	public void tick() {

	}

	@Override
	public void charTyped(char chr, int keyCode) {
		Character typedChar = chr;
		if (focused) {
			if (typedChar != null && text.length() < maxLength)
				setText(getText() + typedChar);
		}
	}

	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

	private String getText() {
		return text;
	}

	private void setText(String text) {
		this.text = text;
	}

}
