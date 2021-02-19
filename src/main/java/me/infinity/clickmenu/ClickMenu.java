package me.infinity.clickmenu;

import me.infinity.InfMain;
import me.infinity.features.module.visual.GuiMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * ClickMenu design from csgo cheats
 * 
 * @author spray
 *
 */
public class ClickMenu extends Screen {

	private Panel panel;

	public ClickMenu() {
		super(new LiteralText(""));
		int x = 20;
		int y = 20;
		panel = new Panel(x, y, 380, 250);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();
		mouseX /= scale;
		mouseY /= scale;
		matrices.scale(scale, scale, scale);
		panel.render(matrices, mouseX, mouseY, delta);
		matrices.scale(1F, 1F, 1F);
		super.render(matrices, mouseX, mouseY, delta);
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
		InfMain.getModuleManager().getModuleByClass(GuiMod.class).setEnabled(false);
		super.onClose();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		panel.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

}
