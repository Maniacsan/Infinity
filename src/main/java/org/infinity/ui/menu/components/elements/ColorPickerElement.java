package org.infinity.ui.menu.components.elements;

import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.components.window.ColorPickerWindow;
import org.infinity.ui.menu.util.Render2D;
import org.infinity.utils.Helper;

import net.minecraft.client.util.math.MatrixStack;

public class ColorPickerElement extends AbstractElement {

	private boolean hovered;

	public ColorPickerElement(Setting setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height);

		IFont.legacy14.drawString(matrices, setting.getName(), x + 22, y + 5.5, 0xFFFFFFFF);
		Render2D.drawBorderedRect(matrices, x + 3, y + 3.5, 16, 12, 0.5f, 0xFF070337, setting.getColor().getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (hovered)
			Helper.openScreen(new ColorPickerWindow(Helper.MC.currentScreen, setting));
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
