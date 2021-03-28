package me.infinity.ui.inventory;

import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class InventoryLogo {

	private static final Identifier LOGO = new Identifier("infinity", "logo.png");
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
		// name
		int cy = Helper.getPlayer().isCreative() && Helper.minecraftClient.currentScreen instanceof AbstractInventoryScreen ? 28 : 0;
		int cx = Helper.getPlayer().isCreative() && Helper.minecraftClient.currentScreen instanceof AbstractInventoryScreen ? 8 : 0;
		
		Helper.minecraftClient.getTextureManager().bindTexture(NAME);
		DrawableHelper.drawTexture(matrices, this.x + 51 + cx, this.y - 28 - cy, 0, 0, 90, 23, 90, 23);

		// logo
		Helper.minecraftClient.getTextureManager().bindTexture(LOGO);
		DrawableHelper.drawTexture(matrices, this.x + 13 + cx, this.y - 37 - cy, 0, 0, 38, 40, 38, 40);
	}

}
