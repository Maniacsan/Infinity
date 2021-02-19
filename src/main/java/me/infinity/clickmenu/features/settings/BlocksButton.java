package me.infinity.clickmenu.features.settings;

import java.awt.Color;

import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;

public class BlocksButton extends SettingButton {

	private boolean hovered;
	private boolean added;

	public BlocksButton(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		int xOffset = 3;
		int yOffset = 0;
		for (Block blocks : setting.getRenderBlocks()) {
			if (added) {
				setting.getBlocks().add(blocks);
			}
			this.hovered = Render2D.isHovered(mouseX, mouseY, xOffset + x + 2, yOffset + y, 21, 19);
			Render2D.drawRectWH(matrices, xOffset + x - 1, yOffset + y - 2, 21, 19, added ? java.awt.Color.BLUE.getRGB() : hovered ? Color.GRAY.getRGB() : 0x90000000);
			Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(blocks.asItem().getDefaultStack(), (int) ((int) xOffset + x + 2),
					(int) ((int) yOffset + y));
			xOffset += 23;
			if (xOffset > 140) {
				xOffset = 3;
				yOffset += 21;
			}
		}
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hovered && button == 0) {
			added = !added;
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

}
