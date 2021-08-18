package org.infinity.ui.menu.components.elements;

import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.components.window.ColorPickerWindow;
import org.infinity.utils.Helper;
import org.infinity.utils.render.Render2D;

import net.minecraft.client.util.math.MatrixStack;

public class CheckBoxElement extends AbstractElement {

	private boolean hovered;
	private double move;

	public CheckBoxElement(Setting setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width + 10, height - 4);

		move = setting.isToggle() ? Math.min(10, move + 2) : Math.max(0, move - 2);
		Render2D.drawHRoundedRect(matrices, x + width - 10, y + 5, 10, 10,
				setting.isToggle() ? 0xFF101E2E : 0xFF191919);
		Render2D.drawCircle(matrices, x + width - 10 + move, y + 10, 7, setting.isToggle() ? 0xFF60B9CF : 0xFF505151);

		IFont.legacy14.drawString(matrices, this.setting.getName(), x + 1, y + 5,
				setting.isToggle() ? 0xFFFFFFFF : 0xFFC4BFBF);

		if (setting.getColor() == null)
			return;

		Render2D.drawBorderedRect(matrices, x + width - 39, y + 5, 17, 10, 0.5f, 0x90000000,
				setting.getColor().getRGB());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (Render2D.isHovered(mouseX, mouseY, x + width - 40, y + 4, 19, 12) && setting.getColor() != null) {
			Helper.openScreen(new ColorPickerWindow(Helper.MC.currentScreen, setting));
		} else if (this.hovered && button == 0) {
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

	public double getMove() {
		return move;
	}

	public void setMove(double move) {
		this.move = move;
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		// TODO Auto-generated method stub

	}
}
