package org.infinity.clickmenu;

import org.infinity.clickmenu.components.Panel;
import org.infinity.ui.IScreen;

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
	private float fade;

	public ClickMenu() {
		panel = new Panel(this, 60, 20, 400, 290);
		anim = 0.28;
	}

	@Override
	public void init() {
		panel.init();

		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		fade = (float) Math.min(1, fade + 0.2);
		anim = anim > 0 ? Math.max(0, anim - 0.14) : 0;

		matrices.push();

		if (anim > 0) {
			matrices.translate(panel.x + 200, panel.y + 145, 0);
			matrices.scale((float) (1 + anim), (float) (1 + anim), (float) (1 + anim));
			matrices.translate(-panel.x - 200, -panel.y - 145, 0);
		}

		RenderSystem.setShaderColor(1f, 1f, 1f, fade);
		panel.render(matrices, mouseX, mouseY, delta);

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
		panel.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
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

		anim = 0.28;
		fade = 0;
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
