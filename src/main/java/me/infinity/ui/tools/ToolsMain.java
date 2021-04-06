package me.infinity.ui.tools;

import me.infinity.InfMain;
import me.infinity.clickmenu.util.FontUtils;
import me.infinity.features.module.hidden.AntiFabric;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ToolsMain extends Screen {

	private Screen parent;

	private ButtonWidget fSpoofWidget;

	public ToolsMain(Screen parent) {
		super(new LiteralText("Tools"));
		this.parent = parent;
	}

	@Override
	public void init() {
		// height / 2 - lastY - 15;

		AntiFabric antiFabric = ((AntiFabric) InfMain.getModuleManager().getModuleByClass(AntiFabric.class));
		addButton(fSpoofWidget = new ButtonWidget(this.width / 2 - 80, this.height / 2 - 90, 160, 20,
				new LiteralText("AntiFabric Spoof: " + onOrOff(antiFabric.isEnabled())), (buttonWidget) -> {
					antiFabric.enable();
					fSpoofWidget.setMessage(new LiteralText("AntiFabric Spoof: " + onOrOff(antiFabric.isEnabled())));
				}));

		addButton(new ButtonWidget(this.width / 2 - 50, this.height / 2 + 60, 100, 20, ScreenTexts.DONE,
				(buttonWidget) -> {
					this.client.openScreen(parent);
				}));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		FontUtils.drawHVCenteredString(matrices, "Tools", this.width / 2, 27, -1);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private String onOrOff(boolean on) {
		return on ? Formatting.GREEN + "ON" : Formatting.GRAY + "OFF";
	}

}
