package org.infinity.clickmenu.widgets;

import org.infinity.clickmenu.util.FontUtils;
import org.infinity.utils.MathAssist;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WSlider extends AbstractButtonWidget {
	protected double value;
	private double min;
	private double max;

	public WSlider(double min, double max, int x, int y, int width, int height, Text text, double value) {
		super(x, y, width, height, text);
		this.min = min;
		this.max = max;
		setValue(value);
	}

	protected int getYImage(boolean hovered) {
		return 0;
	}

	protected MutableText getNarrationMessage() {
		return new TranslatableText("gui.narrate.slider", new Object[] { this.getMessage() });
	}

	protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {

		client.getTextureManager().bindTexture(WIDGETS_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int i = (this.isHovered() ? 2 : 1) * 20;
		
		this.drawTexture(matrices, this.x + (int) (this.value * (double) (this.width - 8)), this.y, 0, 46 + i, 4, 20);
		this.drawTexture(matrices, this.x + (int) (this.value * (double) (this.width - 8)) + 4, this.y, 196, 46 + i, 4,
				20);
		
		FontUtils.drawStringWithShadow(matrices, String.valueOf(MathAssist.round(getValue(), 2)), x + 4 + (value * width - 8), y + 24, -1);
	}

	public void onClick(double mouseX, double mouseY) {
		this.setValueFromMouse(mouseX);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean bl = keyCode == 263;
		if (bl || keyCode == 262) {
			float f = bl ? -1.0F : 1.0F;
			this.setValueMouse(this.value + (double) (f / (float) (this.width - 8)));
		}

		return false;
	}

	private void setValueFromMouse(double mouseX) {
		this.setValueMouse((mouseX - (double) (this.x + 4)) / (double) (this.width - 8));
	}

	private void setValueMouse(double mouseX) {
		this.value = MathHelper.clamp(mouseX, 0.0D, 1.0D);
	}

	public void setValue(double value) {
		this.value = MathHelper.clamp((this.adjust(value) - this.min) / (this.max - this.min), 0.0D, 1.0D);
	}

	private double adjust(double value) {
		if (0.1 > 0.0F) {
			value = (double) (0.1 * (float) Math.round(value / (double) 0.1));
		}

		return MathHelper.clamp(value, this.min, this.max);
	}

	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		this.setValueFromMouse(mouseX);
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	public void playDownSound(SoundManager soundManager) {
	}

	public void onRelease(double mouseX, double mouseY) {
		super.playDownSound(MinecraftClient.getInstance().getSoundManager());
	}

	public double getValue() {
		return this.adjust(MathHelper.lerp(MathHelper.clamp(value, 0.0D, 1.0D), this.min, this.max));
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
