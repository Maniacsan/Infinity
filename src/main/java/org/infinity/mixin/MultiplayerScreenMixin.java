package org.infinity.mixin;

import org.infinity.main.InfMain;
import org.infinity.ui.account.GuiAccountManager;
import org.infinity.ui.menu.widgets.WSlider;
import org.infinity.ui.tools.ToolsMain;
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

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

	@Unique
	private WSlider slider;

	@Unique
	private String currentVersion;

	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initClass(CallbackInfo ci) {
		currentVersion = String.valueOf(ViaFabric.clientSideVersion);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		if (InfMain.INSTANCE.self)
			return;

		ButtonWidget tools = new ButtonWidget(5, 10, 80, 20, new TranslatableText("Tools"), (buttonWidget) -> {
			Helper.minecraftClient.openScreen(new ToolsMain(this));
		});
		addDrawableChild(tools);

		ButtonWidget accButton = new TexturedButtonWidget(this.width / 2 - 180, this.height - 52, 20, 20, 0, 0, 0,
				new Identifier("infinity", "textures/game/screen/alt.png"), 20, 20,
				buttonWidget -> Helper.minecraftClient.openScreen(new GuiAccountManager(this)),
				new TranslatableText("Account"));
		addDrawableChild(accButton);

		slider = new WSlider(0D, ProtocolSorter.getProtocolVersions().size() - 1, width / 2 + 50, 10, 110, 20,
				new LiteralText("Version " + currentVersion), ViaFabric.stateValue);
		addDrawableChild(slider);
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		slider.render(matrices, mouseX, mouseY, delta);

		if (slider.prevValue != slider.getValue()) {
			currentVersion = ProtocolUtils
					.getProtocolName(ProtocolSorter.getProtocolVersions().get((int) slider.getValue()).getVersion());
			ViaFabric.stateValue = slider.getValue();

			Integer parsed = ProtocolUtils.parseProtocolId(currentVersion);
			ViaFabric.clientSideVersion = parsed;
			slider.setMessage(new LiteralText("Version " + currentVersion));
			slider.prevValue = slider.getValue();
		}
	}

}
