package me.infinity.clickmenu.features.settings;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import me.infinity.features.module.visual.XRay;
import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;

public class BlocksButton extends SettingButton {

	public BlocksButton(Settings setting, double x, double y, double width, double height) {
		super(setting, x, y, width, height);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.x = x;
		this.y = y;
		XRay xray = ((XRay) InfMain.getModuleManager().getModuleByClass(XRay.class));
		Render2D.drawRectWH(matrices, x, y, this.width, this.height, 0x90000000);
		for (Block blocks : xray.block.getRenderBlocks()) {
			Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(blocks.asItem().getDefaultStack(), (int) this.x + 2, (int) this.y);
		}
	}

}
