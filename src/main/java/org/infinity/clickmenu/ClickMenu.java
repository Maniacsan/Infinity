package org.infinity.clickmenu;

import org.infinity.clickmenu.components.Panel;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * 
 * @author spray
 *
 */
public class ClickMenu extends Screen {

	public Panel panel;

	public ClickMenu() {
		super(new LiteralText("ClickMenu"));
		panel = new Panel(this, 20, 20, 400, 290);
	}

	@Override
	public void init() {
		panel.init();

		children.add(panel.searchField);
		children.add(panel.configPanel.textField);
		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();

		mouseX /= scale;
		mouseY /= scale;

		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		panel.render(matrices, mouseX, mouseY, delta);
		GL11.glPopMatrix();

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		panel.tick();
		super.tick();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();

		mouseX /= scale;
		mouseY /= scale;

		panel.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();

		mouseX /= scale;
		mouseY /= scale;

		panel.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		panel.mouseScrolled(d, e, amount);
		return super.mouseScrolled(e, e, amount);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		panel.charTyped(chr, keyCode);
		return super.charTyped(chr, keyCode);
	}

	@Override
	public void onClose() {
		panel.onClose();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		panel.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

}
