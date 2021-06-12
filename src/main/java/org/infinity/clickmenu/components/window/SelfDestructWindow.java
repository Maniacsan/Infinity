package org.infinity.clickmenu.components.window;

import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.module.world.SelfDestruct;
import org.infinity.main.InfMain;
import org.infinity.ui.util.CustomButtonWidget;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class SelfDestructWindow extends Screen {

	private Screen prev;
	private double hover;

	public SelfDestructWindow(Screen prev) {
		super(new LiteralText(""));
		this.prev = prev;
	}

	@Override
	protected void init() {
		super.init();
		addButton(new CustomButtonWidget(this.width / 2 - 85, height / 2 + 10, 80, 20, new LiteralText("Yes"),
				buttonWidget -> {
					((SelfDestruct) InfMain.getModuleManager().getModuleByClass(SelfDestruct.class)).destruct();
				}));
		addButton(new CustomButtonWidget(this.width / 2 - 85 + 100, height / 2 + 10, 80, 20, new LiteralText("Cancel"),
				buttonWidget -> {
					InfMain.getModuleManager().getModuleByClass(SelfDestruct.class).setEnabled(false);
					onClose();
				}));
	}

	@Override
	public void onClose() {
		if (InfMain.getModuleManager().getModuleByClass(SelfDestruct.class).isEnabled())
			InfMain.getModuleManager().getModuleByClass(SelfDestruct.class).setEnabled(false);
		Helper.openScreen(prev);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		prev.render(matrices, -1, -1, delta);

		Render2D.drawBorderedRect(matrices, (width / 2) - 100, height / 2 - 65, 210, 105, 1, 0xFF080629, 0xFF161621);

		IFont.legacy16.drawCenteredString(
				"Do you really want to " + Formatting.BLUE + "self-destruct" + Formatting.WHITE + "?", width / 2,
				height / 2 - 45, -1);

		hover = Render2D.isHovered(mouseX, mouseY, (width / 2) + 95, height / 2 - 60, 10, 10)
				? Math.min(1.2, hover + 0.1)
				: Math.max(1, hover - 0.1);

		double hw = (width / 2) + 99;
		double hh = height / 2 - 56;

		GL11.glPushMatrix();
		GL11.glTranslated(hw, hh, 0);
		GL11.glScaled(hover, hover, 1);
		GL11.glTranslated(-hw, -hh, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/exit.png"), (width / 2) + 95,
				height / 2 - 60, 8, 8);

		GL11.glPopMatrix();
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, (width / 2) + 95, height / 2 - 60, 10, 10))
			onClose();
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
