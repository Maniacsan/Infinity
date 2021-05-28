package org.infinity.clickmenu.components.window;

import java.awt.Color;
import java.awt.Rectangle;

import org.infinity.clickmenu.ClickMenu;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WCheckBox;
import org.infinity.clickmenu.widgets.WSlider;
import org.infinity.clickmenu.widgets.WTextField;
import org.infinity.features.Setting;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ColorPicker extends Screen {

	private Screen prev;

	public static final Identifier PICKER = new Identifier("infinity", "textures/game/screen/picker.png");

	private WTextField colorField;
	private WCheckBox rainbowB;
	private WSlider rainbowSpeed;

	private static final int H = 0, S = 1, B = 2;
	private float[] hsb;
	private int rgb;

	private boolean draggingHS, draggingB;

	private Rectangle rectHSArea, rectBArea;

	private int xPosition = 20;
	private int yPosition = 20;

	private Setting setting;

	private double anim;

	public ColorPicker(Screen prev, Setting setting) {
		super(new LiteralText(""));
		this.prev = prev;
		this.setting = setting;

		this.xPosition = (width / 2) - 90;
		this.yPosition = (height / 2) - 110;

		this.rectHSArea = new Rectangle(this.xPosition + 10, this.yPosition + 10, 128, 128);
		this.rectBArea = new Rectangle(this.xPosition + 148, this.yPosition + 10, 7, 128);

		Color color = setting.getColor();
		this.hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		anim = 0.4;
	}

	@Override
	public void init() {

		Helper.minecraftClient.keyboard.setRepeatEvents(true);
		colorField = new WTextField(Helper.minecraftClient.textRenderer, xPosition, yPosition, 40, 14,
				new TranslatableText("#"), false);
		colorField.setColor(0xFF0B1427);
		colorField.setMaxLength(6);

		children.add(colorField);

		rainbowSpeed = new WSlider(1, 10, width / 2, height / 2, 160, 20, new LiteralText("Rainbow Speed"),
				setting.getRainbowSpeed());

		children.add(rainbowSpeed);

		this.rainbowB = new WCheckBox(width / 2, this.yPosition + 98, 150, 20, new LiteralText("Rainbow"),
				setting.isRainbow());
		addButton(rainbowB);

		this.updateColor();
	}

	public int getColor() {
		int rgb = (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
		return rgb;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		anim = anim > 0 ? Math.max(0, anim - 0.11) : 0;

		GL11.glPushMatrix();

		if (anim > 0) {
			GL11.glTranslated(this.xPosition + 97.5, this.yPosition + 135, 0);
			GL11.glScaled(1 + anim, 1 + anim, 1 + anim);
			GL11.glTranslated(-this.xPosition - 97.5, -this.yPosition - 135, 0);
		}
		

		renderPicker(matrices, mouseX, mouseY, delta);

		
		GL11.glPopMatrix();

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void renderPicker(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.xPosition = (width / 2) - 90;
		this.yPosition = (height / 2) - 122;

		this.rectHSArea = new Rectangle(this.xPosition + 10, this.yPosition + 10, 128, 128);
		this.rectBArea = new Rectangle(this.xPosition + 148, this.yPosition + 10, 7, 128);

		this.setting.setRainbow(rainbowB.isChecked());
		this.setting.setRainbowSpeed(rainbowSpeed.getValue());

		if (setting.isRainbow()) {
			this.hsb[H] += Math.floor(setting.getRainbowSpeed()) / 600;

			if (this.hsb[H] > 1)
				this.hsb[H] = 0;
			updateColor();
		}

		int hPos = this.xPosition + 10 + (int) (128F * this.hsb[H]);
		int sPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[S]));
		int bPos = this.yPosition + 10 + (128 - (int) (128F * this.hsb[B]));
		int brightness = Color.HSBtoRGB(this.hsb[H], this.hsb[S], 1.0F) | 0xFF000000;

		Render2D.drawBorderedRect(matrices, this.xPosition - 30, this.yPosition - 30, 225, 300, 1, 0xFF080629,
				0xFF161621);
		Render2D.drawBorderedRect(matrices, this.xPosition, this.yPosition, 164, 165, 2, 0xFF131D4C, 0xFF121D39);

		fill(matrices, this.xPosition + 9, this.yPosition + 9, this.xPosition + 139, this.yPosition + 139, 0xFF2E4354);
		RenderUtil.drawTexture(matrices, PICKER, this.xPosition + 10, this.yPosition + 10, 128, 128);

		fillGradient(matrices, this.xPosition + 148, this.yPosition + 10, this.xPosition + 155, this.yPosition + 138,
				brightness, 0xFF000000);

		Render2D.drawTriangle(xPosition + 146, bPos, 2, 90, 0xFFE4E4E4);
		Render2D.drawTriangle(xPosition + 157, bPos, 2, -90, 0xFFE4E4E4);

		fill(matrices, this.xPosition + 10, this.yPosition + 143, this.xPosition + 50, this.yPosition + 157,
				0xFF000000 | this.rgb);

		FontUtils.drawString(matrices, "#", this.xPosition + 70, this.yPosition + 147, -1);

		colorField.setX(xPosition + 81);
		colorField.setY(yPosition + 143);
		colorField.setWidth(57);
		colorField.setHeight(14);
		colorField.render(matrices, mouseX, mouseY, delta);

		Render2D.drawBorderedRect(matrices, this.xPosition, this.yPosition + 168, 165, 70, 2, 0x90131D4C, 0x90141F46);

		Render2D.drawBorderedCircle(hPos, sPos, 4, 1, 0xFFFFFFFF, 0xFF000000 | this.rgb);

		this.rainbowB.setX(xPosition + 2);
		this.rainbowB.setY(yPosition + 172);
		this.rainbowB.render(matrices, mouseX, mouseY, delta);

		this.rainbowSpeed.setX(xPosition + 2);
		this.rainbowSpeed.setY(yPosition + 199);
		this.rainbowSpeed.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.draggingHS) {
			this.hsb[H] = clamp((float) (mouseX - this.xPosition - 10), 0, 128) / 128F;
			this.hsb[S] = (128F - clamp((float) (mouseY - this.yPosition - 10), 0, 128)) / 128F;
			this.updateColor();
		}

		if (this.draggingB) {
			this.hsb[B] = (128F - clamp((float) (mouseY - this.yPosition - 10), 0, 128)) / 128F;
			this.updateColor();
		}

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.rectHSArea.contains(mouseX, mouseY))
			this.draggingHS = true;

		if (this.rectBArea.contains(mouseX, mouseY))
			this.draggingB = true;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.draggingHS = false;
		this.draggingB = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		return super.mouseScrolled(e, e, amount);
	}

	@Override
	public void onClose() {
		anim = 0.4;
		Helper.openScreen(InfMain.INSTANCE.init.menu);
	}

	@Override
	public void tick() {
		super.tick();

	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (colorField.keyPressed(keyCode, scanCode, modifiers) && keyCode == GLFW.GLFW_KEY_ENTER) {
			updateColorFromTextEntry();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	protected void updateColor() {
		this.rgb = (0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
		String hex = String.format("%06x", 0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B]));
		colorField.setText(hex.toUpperCase());

		setting.setColor((0xFFFFFF & Color.HSBtoRGB(this.hsb[H], this.hsb[S], this.hsb[B])));
	}

	protected void updateColorFromTextEntry() {
		if (colorField.getText().length() < 6)
			return;

		int currentRed = (this.rgb >> 16) & 0xFF;
		int currentGreen = (this.rgb >> 8) & 0xFF;
		int currentBlue = this.rgb & 0xFF;

		String hex = "#" + colorField.getText();
		currentRed = Integer.valueOf(hex.substring(1, 3), 16);
		currentGreen = Integer.valueOf(hex.substring(3, 5), 16);
		currentBlue = Integer.valueOf(hex.substring(5, 7), 16);

		this.hsb = Color.RGBtoHSB(currentRed, currentGreen, currentBlue, null);
		this.updateColor();
	}

	public static float clamp(float value, float min, float max) {
		return Math.min(Math.max(value, min), max);
	}
}
