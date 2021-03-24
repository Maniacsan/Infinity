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

	public InventoryLogo(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void onRender(MatrixStack matrices) {
		// name
		Helper.minecraftClient.getTextureManager().bindTexture(NAME);
		DrawableHelper.drawTexture(matrices, this.width / 2 - 36, this.height / 2 - 121, 0, 0, 90, 23, 90, 23);

		// logo
		Helper.minecraftClient.getTextureManager().bindTexture(LOGO);
		DrawableHelper.drawTexture(matrices, this.width / 2 - 75, this.height / 2 - 130, 0, 0, 38, 40, 38, 40);
	}

}
