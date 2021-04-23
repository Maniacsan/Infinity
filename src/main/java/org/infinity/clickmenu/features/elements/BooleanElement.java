package org.infinity.clickmenu.features.elements;

import java.awt.Color;

import org.infinity.InfMain;
import org.infinity.clickmenu.features.SettingElement;
import org.infinity.clickmenu.util.ColorUtils;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Settings;
import org.infinity.features.module.visual.GuiMod;

import net.minecraft.client.util.math.MatrixStack;

public class BooleanElement extends SettingElement {

	private boolean hovered;

	public BooleanElement(Settings setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.height = height;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height - 5);

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
}
