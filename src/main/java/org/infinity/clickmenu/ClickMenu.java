package org.infinity.clickmenu;

import org.infinity.clickmenu.components.Panel;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.infinity.ui.IScreen;
import org.infinity.utils.Helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;

/**
 * 
 * @author spray
 *
 */
public class ClickMenu extends IScreen {

	public Panel panel;
	public double anim;

	public ClickMenu() {
		panel = new Panel(this, 60, 20, 400, 290);
	}

	@Override
	public void init() {
		anim = 0.32;

		panel.init();

		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();

		anim = anim > 0 ? Math.max(0, anim - 0.14) : 0;

		mouseX /= scale;
		mouseY /= scale;

		matrices.push();

		if (anim > 0) {
			matrices.translate(panel.x + 200, panel.y + 145, 0);
			matrices.scale((float) (scale + anim), (float) (scale + anim), (float) (scale + anim));
			matrices.translate(-panel.x - 200, -panel.y - 145, 0);
		}

		matrices.push();

		matrices.scale(scale, scale, scale);

		panel.render(matrices, mouseX, mouseY, delta);

		matrices.pop();

		matrices.pop();

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

	public void startScissor(MatrixStack matrices, double x, double y, double width, double height) {
		float scale = (float) (((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale() + anim);
		double scaleWidth = (double) Helper.minecraftClient.getWindow().getWidth()
				/ Helper.minecraftClient.getWindow().getScaledWidth();
		double scaleHeight = (double) Helper.minecraftClient.getWindow().getHeight()
				/ Helper.minecraftClient.getWindow().getScaledHeight();

		scaleWidth *= scale;
		scaleHeight *= scale;

		RenderSystem.enableScissor((int) (x * scaleWidth),
				(int) ((Helper.minecraftClient.getWindow().getHeight()) - (int) ((y + height) * scaleHeight)),
				(int) (width * scaleWidth), (int) (height * scaleHeight));
	}

}
