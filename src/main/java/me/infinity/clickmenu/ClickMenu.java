package me.infinity.clickmenu;

import org.lwjgl.opengl.GL11;

import me.infinity.InfMain;
import me.infinity.features.module.visual.GuiMod;
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
		super(new LiteralText(""));
	}
	
	public void init() {
		panel = new Panel(this, 20, 20, 380, 250);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		float scale = ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).getScale();
		
		mouseX /= scale;
		mouseY /= scale;
		
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		panel.render(matrices, mouseX, mouseY, delta);
		GL11.glPopMatrix();
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
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		panel.keyPressed(keyCode, scanCode, modifiers);
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

}
