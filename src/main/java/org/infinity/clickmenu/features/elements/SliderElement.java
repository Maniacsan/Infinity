package org.infinity.clickmenu.features.elements;

import org.infinity.InfMain;
import org.infinity.clickmenu.features.SettingElement;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Settings;
import org.infinity.features.module.visual.GuiMod;

import net.minecraft.client.util.math.MatrixStack;

public class SliderElement extends SettingElement {

	public double selected;
	public boolean dragging;
	public boolean hovered;

	// update slider
	protected double width;

	public SliderElement(Settings setting) {
		super(setting);
	}

	public String getRenderValue() {
		return null;
	}

	public void setValue(int mouseX, double x, double width) {
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, double x, double y, double width,
			double height) {
		this.height = height;
		this.width = width;
		this.hovered = Render2D.isHovered(mouseX, mouseY, x + 2, y, width, height);

		String sname = setting.getName();
		String setstrg = String.valueOf(String.valueOf(sname.substring(0, 1).toUpperCase()))
				+ sname.substring(1, sname.length());

		FontUtils.drawStringWithShadow(matrices, setstrg, (float) (x + 2), (float) (y), -1);
		FontUtils.drawHVCenteredString(matrices, this.getRenderValue(), (float) (x + width + 17), (float) (y + 14.0),
				-1);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width, 2, -2130706433);
		Render2D.drawRectWH(matrices, x + 2, y + 12, width * this.selected, 2,
				((GuiMod) InfMain.getModuleManager().getModuleByClass(GuiMod.class)).color.getColor().getRGB());
		Render2D.drawFullCircle(x + 1 + width * selected, y + 13, 3, 0xFFCCD6C8);

		if (!this.dragging)
			return;
		this.setValue(mouseX, x, width);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && hovered) {
			this.dragging = true;
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.dragging = false;
	}

	@Override
	public boolean isVisible() {
		return this.setting.isVisible();
	}

	@Override
	public void mouseScrolled(double d, double e, double amount) {
	}

	@Override
	public void onClose() {
		this.dragging = false;	
	}
}
