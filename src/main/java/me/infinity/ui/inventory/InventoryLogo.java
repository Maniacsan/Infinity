package me.infinity.ui.inventory;

import me.infinity.utils.Helper;
import net.minecraft.client.gui.DrawableHelper;
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
		Helper.minecraftClient.getTextureManager().bindTexture(NAME);
		DrawableHelper.drawTexture(matrices, this.x + 51, this.y - 28, 0, 0, 90, 23, 90, 23);

		// logo
		Helper.minecraftClient.getTextureManager().bindTexture(LOGO);
		DrawableHelper.drawTexture(matrices, this.x + 13, this.y - 37, 0, 0, 38, 40, 38, 40);
	}

}
