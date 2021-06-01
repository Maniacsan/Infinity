package org.infinity.clickmenu.components.elements;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Setting;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;

public class ComboBoxElement extends AbstractElement {

	private boolean addHovered;
	private boolean open;
	public double dropY;
	public double dropX;

	public ComboBoxElement(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.addHovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		Render2D.drawRectWH(matrices, (float) (x), (float) (y), (float) (width), (float) (height), 0x40090C13);
		IFont.legacy15.drawString(setting.getName(), x + 4, y + 6, -1);
		IFont.legacy15.drawStringWithShadow(setting.getCurrentMode(),
				x + width - FontUtils.getStringWidth(setting.getCurrentMode()) - 10, y + 6, -1);

		dropY = y + height;

		if (dropX > 0.4)
			Render2D.drawRectWH(matrices, (float) (x + 3), (float) (dropY), (float) (width - 6), (float) ((dropX)),
					0x40090C13);

		if (dropX >= (setting.getModes().size() - 1) * 18.7) {
			double yOffset = 0;
			for (String mode : this.setting.getModes()) {
				if (setting.getCurrentMode().equalsIgnoreCase(mode))
					continue;
				IFont.legacy14.drawStringWithShadow(mode, x + 10, dropY + yOffset + 5, -1);
				yOffset += 19;
			}
		}
		if (isOpen())
			dropX = RenderUtil.animate(((this.setting.getModes().size() - 1) * 19), dropX, 0.8);
		else
			dropX = RenderUtil.animate(0, dropX, 0.95);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (this.addHovered)
			setOpen(!isOpen());

		if (!isOpen() || button != 0)
			return;

		double yOffset = 0;
		for (String mode : setting.getModes()) {
			if (setting.getCurrentMode().equalsIgnoreCase(mode))
				continue;
			if (Render2D.isHovered(mouseX, mouseY, x + 3, dropY + yOffset, width - 6, 19)) {
				setting.setCurrentMode(mode);
			}
			yOffset += 19;
		}

	}

	@Override
	public boolean isVisible() {
		return setting.isVisible();
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

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		// TODO Auto-generated method stub

	}
}
