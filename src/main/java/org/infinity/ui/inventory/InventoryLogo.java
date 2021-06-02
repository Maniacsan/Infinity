package org.infinity.ui.inventory;

import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class InventoryLogo {

	private static final Identifier LOGO = new Identifier("infinity", "logoneon.png");
	private static final Identifier NAME = new Identifier("infinity", "infinity.png");

	private int width;
	private int height;
	private int x;
	private int y;

	public InventoryLogo(int width, int height, int x, int y) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	public void onRender(MatrixStack matrices) {
		int cy = Helper.getPlayer().isCreative()
				&& Helper.minecraftClient.currentScreen instanceof AbstractInventoryScreen ? 28 : 0;
		int cx = Helper.getPlayer().isCreative()
				&& Helper.minecraftClient.currentScreen instanceof AbstractInventoryScreen ? 8 : 0;

		
		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/game/inventory/infinity.png"), this.x + 53 + cx, this.y - 30 - cy, 90, 23);

		
		RenderUtil.drawTexture(matrices, new Identifier("infinity", "textures/game/logoneon.png"), this.x + 12 + cx, this.y - 39 - cy, 38, 38);
	}

}
