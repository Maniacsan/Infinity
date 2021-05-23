package org.infinity.clickmenu.components.elements;

import java.awt.Color;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.ColorUtils;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Setting;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;

import net.minecraft.client.util.math.MatrixStack;

public class CheckBoxElement extends AbstractElement {

	private boolean hovered;
	private int move;

	public CheckBoxElement(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height - 4);

		Render2D.drawRectWH(matrices, x + 2, y + 0, 10, 10,
				setting.isToggle()
						? ((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).color.getColor().getRGB()
						: 0xFFFFFFFF);
		Render2D.drawRectWH(matrices, x + 3, y + 1, 8, height - 9, ColorUtils.backNight);
		if (setting.isToggle()) {
			Render2D.drawRectWH(matrices, x + 4, y + 2, 6, 6,
					((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).color.getColor().getRGB());
		}
		FontUtils.drawStringWithShadow(matrices, this.setting.getName(), x + 17, y + 1.5, Color.WHITE.getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.hovered && button == 0) {
			this.setting.setToggle(!this.setting.isToggle());
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

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
}
