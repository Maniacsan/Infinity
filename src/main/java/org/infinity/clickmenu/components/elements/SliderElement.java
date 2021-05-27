package org.infinity.clickmenu.components.elements;

import org.infinity.clickmenu.components.Panel;
import org.infinity.clickmenu.components.base.AbstractElement;
import org.infinity.clickmenu.util.FontUtils;
import org.infinity.clickmenu.util.Render2D;
import org.infinity.clickmenu.widgets.WTextField;
import org.infinity.features.Setting;
import org.infinity.features.module.visual.GuiMod;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class SliderElement extends AbstractElement {

	protected WTextField valueField;
	protected boolean dragging;
	protected boolean hovered;

	public SliderElement(Setting setting, Panel panel) {
		super(setting, panel);
		valueField = new WTextField(Helper.minecraftClient.textRenderer, (int) (x + width - 34), (int) (y + 7), 40, 14,
				new TranslatableText("Value"), false);
		valueField.setColor(0xFF0B1427);
		valueField.setMaxLength(12);
	}

	@Override
	public void init() {
	}

	public String getRenderValue() {
		return null;
	}

	public void tick() {
		valueField.tick();
	}

	public void setValue(int mouseX, double x, double width) {
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.hovered = Render2D.isHovered(mouseX, mouseY, x, y, width - 45, height);

		String sname = setting.getName();
		String setstrg = String.valueOf(String.valueOf(sname.substring(0, 1).toUpperCase()))
				+ sname.substring(1, sname.length());

		valueField.setX((int) (x + width - 34));
		valueField.setY((int) (y + 8));
		valueField.setWidth((int) (40));
		valueField.setHeight(14);

		valueField.render(matrices, mouseX, mouseY, delta);

		if (animation < 0)
			animation = 0;
		else if (animation > 1)
			animation = 1;

		FontUtils.drawStringWithShadow(matrices, setstrg, x, y, -1);
		Render2D.drawRectWH(matrices, x, y + 14, width - 45, 1, 0xFF0D1A2C);
		Render2D.drawRectWH(matrices, x, y + 14, (width - 45) * this.animation, 1, 0xFF30639F);

		Render2D.drawBorderedCircle((float) (x + (width - 45) * animation), (float) (y + 14), 4.5F, 1, 0xFF30639F,
				0xFFCCD6C8);

		if (!this.dragging)
			return;
		this.setValue(mouseX, x, width - 45);
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

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		// TODO Auto-generated method stub

	}

	public WTextField getValueField() {
		return valueField;
	}
}
