package org.infinity.clickmenu.components.elements;

import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WTextField;
import org.infinity.features.Setting;
import org.infinity.ui.util.font.IFont;
import org.infinity.utils.Helper;
import org.infinity.utils.render.RenderUtil;

import net.minecraft.client.util.math.MatrixStack;

public class SliderElement extends AbstractElement {

	protected WTextField valueField;
	protected boolean dragging;
	protected boolean hovered;

	public SliderElement(Setting setting) {
		super(setting);
		Helper.minecraftClient.keyboard.setRepeatEvents(true);
		valueField = new WTextField(0xFF0B1427, true);
		valueField.setMaxLength(12);
	}

	@Override
	public void init() {
	}

	public String getRenderValue() {
		return null;
	}

	public void tick() {
	}

	public void setValue(int mouseX, double x, double width) {
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x + 55, y, width - 75, height);

		String sname = setting.getName();
		String setstrg = String.valueOf(String.valueOf(sname.substring(0, 1).toUpperCase()))
				+ sname.substring(1, sname.length());

		valueField.setX((int) (x + width - 15));
		valueField.setY((int) (y + 4));
		valueField.setWidth((int) (30));
		valueField.setHeight(14);

		valueField.render(matrices, mouseX, mouseY, delta);

		if (animation < 0)
			animation = 0;
		else if (animation > 1)
			animation = 1;
		
		float hover = (float) (hovered ? RenderUtil.animate(3, 4, 0.1) : 3);

		IFont.legacy14.drawString(matrices, setstrg, x + 1, y + 6, -1);
		Render2D.drawRectWH(matrices, x + 55, y + 10, width - 75, 1, 0xFF0C1535);
		Render2D.drawRectWH(matrices, x + 55, y + 10, (width - 75) * this.animation, 1, 0xFF30639F);

		Render2D.drawBorderedCircle((float) (x + 55 + (width - 75) * animation), (float) (y + 10), hover, 2, 0xFF5574E5, 0xFFCCD6C8);

		if (!this.dragging)
			return;
		this.setValue(mouseX, x + 55, width - 75);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && hovered) {
			this.dragging = true;
		}
		valueField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		this.dragging = false;
	}
	
	@Override
	public void charTyped(char chr, int keyCode) {
		valueField.charTyped(chr, keyCode);
		super.charTyped(chr, keyCode);
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
		valueField.onClose();
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		valueField.keyPressed(keyCode, scanCode, modifiers);
	}

	public WTextField getValueField() {
		return valueField;
	}
}
