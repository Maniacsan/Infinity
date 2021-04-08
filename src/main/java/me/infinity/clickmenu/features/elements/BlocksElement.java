package me.infinity.clickmenu.features.elements;

import java.awt.Color;

import me.infinity.InfMain;
import me.infinity.clickmenu.features.SettingElement;
import me.infinity.clickmenu.util.Render2D;
import me.infinity.features.Settings;
import me.infinity.features.module.visual.GuiMod;
import me.infinity.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;

public class BlocksElement extends SettingElement {

	public double x, y;

	public BlocksElement(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.height = height;
		this.x = x;
		int xOffset = 3;
		int yOffset = 0;
		for (Block blocks : setting.getRenderBlocks()) {
			boolean hover = Render2D.isHovered(mouseX, mouseY, xOffset + x, yOffset + y, 21, 19);
			boolean isAdded = setting.getBlocks().contains(blocks);
			this.y = y;
			Render2D.drawRectWH(matrices, xOffset + x - 1, yOffset + y - 2, 21, 19, isAdded
					? ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).color.getColor().getRGB() : hover ? Color.GRAY.getRGB(): 0x70000000);
			Helper.minecraftClient.getItemRenderer().renderGuiItemIcon(blocks.asItem().getDefaultStack(),
					(int) ((int) xOffset + x + 2), (int) ((int) yOffset + y));
			xOffset += 23;
			if (xOffset > 140) {
				xOffset = 3;
				yOffset += 21;
			}
		}
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		int xOffset = 3;
		int yOffset = 0;
		for (Block blocks : setting.getRenderBlocks()) {
			if (Render2D.isHovered(mouseX, mouseY, xOffset + x, yOffset + y, 21, 19)) {
				if (button == 0) {
					if (setting.getBlocks().contains(blocks)) {
						setting.getBlocks().remove(blocks);
						//System.out.println("Removed " + blocks.getName());
					} else {
						setting.getBlocks().add(blocks);
						//System.out.println("added " + blocks.getName());
					}
				}
			}
			xOffset += 23;
			if (xOffset > 140) {
				xOffset = 3;
				yOffset += 21;
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		return this.setting.isVisible();
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseScrolled(double d, double e, double amount) {
		// TODO Auto-generated method stub
		
	}

}
