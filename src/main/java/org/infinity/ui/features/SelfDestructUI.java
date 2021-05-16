package org.infinity.ui.features;

import org.infinity.InfMain;
import org.infinity.features.module.world.SelfDestruction;

import net.minecraft.class_5489;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SelfDestructUI extends Screen {

	private static final Text MESSAGE;
	private class_5489 lines;

	public SelfDestructUI() {
		super(new LiteralText(""));
		this.lines = class_5489.field_26528;
	}

	protected void init() {
		super.init();
		this.lines = class_5489.method_30890(this.textRenderer, MESSAGE, this.width - 50);
		int var10000 = this.lines.method_30887() + 1;
		int i = var10000 * 9 * 2;
		this.addButton(
				new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, new LiteralText("Yes"), (buttonWidget) -> {
					((SelfDestruction) InfMain.getModuleManager().getModuleByClass(SelfDestruction.class)).destruct();
					this.client.currentScreen.onClose();
				}));
		this.addButton(
				new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, (buttonWidget) -> {
					this.client.currentScreen.onClose();
					InfMain.getModuleManager().getModuleByClass(SelfDestruction.class).setEnabled(false);
				}));
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		class_5489 var10000 = this.lines;
		this.textRenderer.getClass();
		var10000.method_30893(matrices, this.width / 2 - 90, 70, 9 * 2, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	static {
		MESSAGE = new LiteralText(
				"Do you really want to" + Formatting.BLUE + " self-destruct" + Formatting.RESET + "? ");
	}

}
