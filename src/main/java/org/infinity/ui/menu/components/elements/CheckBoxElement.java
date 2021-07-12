package org.infinity.ui.menu.components.elements;

import org.infinity.features.Setting;
import org.infinity.font.IFont;
import org.infinity.ui.menu.components.base.AbstractElement;
import org.infinity.ui.menu.util.Render2D;

import net.minecraft.client.util.math.MatrixStack;

public class CheckBoxElement extends AbstractElement {

	private boolean hovered;
	private double move;

	public CheckBoxElement(Setting setting) {
		super(setting);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height - 4);
		
		move = setting.isToggle() ? Math.min(10, move + 2) : Math.max(0, move - 2);
		Render2D.drawHRoundedRect(matrices, x + width - 9, y + 5, 10, 10, setting.isToggle() ? 0xFF101E2E : 0xFF191919);

		Render2D.drawCircle(matrices, x + width - 9 + move, y + 10, 7, setting.isToggle() ? 0xFF60B9CF : 0xFF505151);


		IFont.legacy14.drawString(matrices, this.setting.getName(), x + 1, y + 6, setting.isToggle() ? 0xFFFFFFFF : 0xFFC4BFBF);
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
