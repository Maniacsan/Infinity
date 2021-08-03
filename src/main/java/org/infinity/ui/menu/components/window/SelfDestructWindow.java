package org.infinity.ui.menu.components.window;

import org.infinity.features.module.misc.SelfDestruct;
import org.infinity.font.IFont;
import org.infinity.main.InfMain;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.ui.util.CustomButtonWidget;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

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
		addDrawableChild(new CustomButtonWidget(this.width / 2 - 45, height / 2 + 10, 35, 18, new LiteralText("Yes"),
				buttonWidget -> {
					((SelfDestruct) InfMain.getModuleManager().get(SelfDestruct.class)).destruct();
				}));
		addDrawableChild(new CustomButtonWidget(this.width / 2 - 75 + 100, height / 2 + 10, 35, 18,
				new LiteralText("Cancel"), buttonWidget -> {
					InfMain.getModuleManager().get(SelfDestruct.class).setEnabled(false);
					onClose();
				}));
	}

	@Override
	public void onClose() {
		if (InfMain.getModuleManager().get(SelfDestruct.class).isEnabled())
			InfMain.getModuleManager().get(SelfDestruct.class).setEnabled(false);
		Helper.openScreen(prev);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		prev.render(matrices, -1, -1, delta);

		Render2D.drawBorderedRect(matrices, (width / 2) - 100, height / 2 - 65, 210, 105, 1, 0xFF080629, 0xFF161621);

		IFont.legacy16.drawCenteredString(matrices,
				"Do you really want to " + Formatting.BLUE + "self-destruct" + Formatting.WHITE + "?", width / 2 + 10,
				height / 2 - 45, -1);

		hover = Render2D.isHovered(mouseX, mouseY, (width / 2) + 95, height / 2 - 60, 10, 10)
				? Math.min(1.2, hover + 0.1)
				: Math.max(1, hover - 0.1);

		double hw = (width / 2) + 99;
		double hh = height / 2 - 56;

		matrices.push();
		matrices.translate(hw, hh, 0);
		matrices.scale((float) hover, (float) hover, 1f);
		matrices.translate(-hw, -hh, 0);

		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/icons/exit.png"), (width / 2) + 95,
				height / 2 - 60, 8, 8);

		matrices.pop();
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, (width / 2) + 95, height / 2 - 60, 10, 10))
			onClose();
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
