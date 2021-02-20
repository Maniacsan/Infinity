package me.infinity.clickmenu.features.settings;

import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;

public class BlocksListenerButton {

	
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, Block blocks, double x, double y,
			double width, double height) {
		Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(blocks.asItem().getDefaultStack(),
				(int) ((int) x + 2), (int) ((int) y));
	}
}
