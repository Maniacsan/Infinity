package me.infinity.ui.console;

import java.util.ArrayList;

import me.infinity.clickmenu.util.FontUtils;
import me.infinity.clickmenu.util.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class Console extends Screen {

	private ArrayList<String> messages = new ArrayList<>();
	private ArrayList<ConsoleButton> button = new ArrayList<>();
	private TextField textField;
	private double x;
	private double y;

	private double prevX;
	private double prevY;
	private boolean dragging;
	private boolean hovered;

	public Console() {
		super(new LiteralText(""));
		this.x = 60;
		this.y = 20;
	}

	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.textField = new TextField(this.x, this.y, 270);
		this.button.add(new ConsoleButton());
	}

	public void resize(MinecraftClient client, int width, int height) {
		this.init(client, width, height);
	}

	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		this.textField.keyPressed(keyCode, scanCode, modifiers);
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode != 257 && keyCode != 335) {
			if (keyCode == 267) {
				return true;
			} else {
				return false;
			}
		} else {
			String string = this.textField.getText().trim();
			if (!string.isEmpty()) {
				this.sendMessage(string);
				this.messages.add(string);
			}
			return true;
		}
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount > 1.0D) {
			amount = 1.0D;
		}

		if (amount < -1.0D) {
			amount = -1.0D;
		}

		if (!hasShiftDown()) {
			amount *= 7.0D;
		}
		return true;
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (hovered) {
				dragging = true;
				this.prevX = this.x - mouseX;
				this.prevY = this.y - mouseY;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	protected void insertText(String text, boolean override) {
		if (override) {
			this.textField.setText(text);
		}

	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, this.x, this.y, x + 270, y + 180);
		if (this.dragging) {
			this.x = this.prevX + mouseX;
			this.y = this.prevY + mouseY;
		}
		
		if (messages.size() > 12) {
			messages.remove(messages.size() - 1);
		}

		fill(matrices, (int) x, (int) y + 181, (int) x + 270, (int) y + 194, 0x90000000);
		fill(matrices, (int) x, (int) y, (int) x + 270, (int) y + 180, 0xFF000000);
		this.textField.render(matrices, mouseX, mouseY, delta, x + 2, y + 183);
		double yOffset = y + 2;
		for (ConsoleButton button : button) {
			for (String historyTexts : this.messages)
				button.render(matrices, historyTexts, x, yOffset);
			yOffset += 15;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.dragging = false;
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		textField.charTyped(chr, keyCode);
		return false;
	}

	public boolean isPauseScreen() {
		return false;
	}

	private void setText(String text) {
		this.textField.setText(text);
	}

}
