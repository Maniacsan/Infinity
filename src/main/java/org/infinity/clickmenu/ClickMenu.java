package org.infinity.clickmenu;

import java.util.List;

import org.infinity.clickmenu.components.Panel;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.infinity.ui.IScreen;
import org.infinity.utils.Helper;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

/**
 * 
 * @author spray
 *
 */
public class ClickMenu extends IScreen {

	public Panel panel;
	public double anim;
	private boolean closeAnim;

	public ClickMenu() {
		Window w = Helper.minecraftClient.getWindow();
		panel = new Panel(this, w.getScaledWidth() / 2 - 230, w.getScaledHeight() / 2 - 154, 400, 290);
	}

	@Override
	public void init() {
		anim = 0.32;

		panel.init();
		panel.addChildren(children);

		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();

		anim = anim > 0 ? Math.max(0, anim - 0.14) : 0;

		mouseX /= scale;
		mouseY /= scale;

		GL11.glPushMatrix();

		if (anim > 0) {
			GL11.glTranslated(panel.x + 200, panel.y + 145, 0);
			GL11.glScaled(scale + anim, scale + anim, scale + anim);
			GL11.glTranslated(-panel.x - 200, -panel.y - 145, 0);
		}

		GL11.glPushMatrix();

		GL11.glScaled(scale, scale, scale);

		panel.render(matrices, mouseX, mouseY, delta);

		GL11.glPopMatrix();

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

		anim = 0.32;
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

	public List<Element> getChildren() {
		return children;
	}

	public void startScissor(double x, double y, double width, double height) {
		float scale = (float) (((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale() + anim);
		double scaleWidth = (double) Helper.minecraftClient.getWindow().getWidth()
				/ Helper.minecraftClient.getWindow().getScaledWidth();
		double scaleHeight = (double) Helper.minecraftClient.getWindow().getHeight()
				/ Helper.minecraftClient.getWindow().getScaledHeight();

		scaleWidth *= scale;
		scaleHeight *= scale;

		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glScissor((int) (x * scaleWidth),
				(int) ((Helper.minecraftClient.getWindow().getHeight()) - (int) ((y + height) * scaleHeight)),
				(int) (width * scaleWidth), (int) (height * scaleHeight));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

}
