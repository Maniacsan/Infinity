package org.infinity.clickmenu.components.elements;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.components.window.ColorPicker;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Setting;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.client.util.math.MatrixStack;

public class ColorPickerElement extends AbstractElement {

	private boolean hovered;

	public ColorPickerElement(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);
		
		Render2D.drawRectWH(matrices, x, y, width, height, 0x90000000);

		FontUtils.drawStringWithShadow(matrices, setting.getName(), x + 3, y + 5, -1);
		Render2D.drawBorderedRect(matrices, x + width - 25, y + 4, 22, 10, 2, 0xFF070337, setting.getColor().getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered)
			Helper.openScreen(new ColorPicker(InfMain.INSTANCE.init.menu, setting));
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {

	}

	@Override
	public void mouseScrolled(double d, double e, double amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVisible() {
		return setting.isVisible();
	}

	@Override
	public void onClose() {

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		// TODO Auto-generated method stub

	}
}
