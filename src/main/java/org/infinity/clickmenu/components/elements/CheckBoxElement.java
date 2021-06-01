package org.infinity.clickmenu.components.elements;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.features.Setting;
import org.infinity.ui.util.font.IFont;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.util.math.MatrixStack;

public class CheckBoxElement extends AbstractElement {

	private boolean hovered;
	private double move;

	public CheckBoxElement(Setting setting, Panel panel) {
		super(setting, panel);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width, height - 4);
		
		GL11.glPushMatrix();
		
		IFont.legacy14.drawStringWithShadow(this.setting.getName(), x + 22, y + 6, -1);

		move = setting.isToggle() ? Math.min(10, move + 2) : Math.max(0, move - 2);
		Render2D.drawAngleRect(matrices, x + 4, y + 6, 10, 8, setting.isToggle() ? 0xFF101E2E : 0xFF191919);

		Render2D.drawCircle(x + 4 + move, y + 10, 5, setting.isToggle() ? 0xFF60B9CF : 0xFF505151);
		GL11.glPopMatrix();
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
