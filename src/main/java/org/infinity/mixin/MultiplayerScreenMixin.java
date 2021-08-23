package org.infinity.mixin;

import org.infinity.main.InfMain;
import org.infinity.ui.account.GuiAccountManager;
import org.infinity.ui.menu.widgets.WSlider;
import org.infinity.ui.tools.ToolsUI;
import org.infinity.utils.Helper;
import org.infinity.via.ViaFabric;
import org.infinity.via.util.ProtocolSorter;
import org.infinity.via.util.ProtocolUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

	@Unique
	private WSlider slider;

	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		if (InfMain.INSTANCE.self)
			return;

		ButtonWidget tools = new ButtonWidget(5, 10, 80, 20, new TranslatableText("Tools"), (buttonWidget) -> {
			Helper.MC.openScreen(new ToolsUI(this));
		});
		addDrawableChild(tools);

		ButtonWidget accButton = new TexturedButtonWidget(this.width / 2 - 180, this.height - 52, 20, 20, 0, 0, 0,
				new Identifier("infinity", "textures/game/screen/alt.png"), 20, 20,
				buttonWidget -> Helper.MC.openScreen(new GuiAccountManager(this)), new TranslatableText("Account"));
		addDrawableChild(accButton);

		slider = new WSlider(0D, ProtocolSorter.getProtocolVersions().size() - 1,
				width / 2 + 50, 10, 110, 20, new LiteralText("Version " + ViaFabric.CURRENT_VERSION),
				ViaFabric.stateValue);
		addDrawableChild(slider);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		slider.render(matrices, mouseX, mouseY, delta);

		if (slider.prevValue != slider.getValue()) {
			ViaFabric.CURRENT_VERSION = ProtocolUtils
					.getProtocolName(ProtocolSorter.getProtocolVersions().get((int) slider.getValue()).getVersion());
			ViaFabric.stateValue = slider.getValue();

			Integer parsed = ProtocolUtils.parseProtocolId(ViaFabric.CURRENT_VERSION);
			ViaFabric.INSTANCE.setVersion(parsed);
			slider.setMessage(new LiteralText("Version " + ViaFabric.CURRENT_VERSION));
			slider.prevValue = slider.getValue();
		}
	}

}
