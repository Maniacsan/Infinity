package me.infinity.clickmenu.features.settings;

import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;

public class BlocksButton extends SettingButton {
	
	public BlocksButton(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		Render2D.drawRectWH(matrices, x, y, width, height, 0x90000000);
		for (Block blocks : setting.getRenderBlocks()) {
			Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(blocks.asItem().getDefaultStack(), (int) x + 2, (int) y);
		}
	}

}
