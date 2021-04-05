package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.infinity.ui.account.GuiAccountManager;
import me.infinity.ui.tools.ToolsMain;
import me.infinity.utils.Helper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {

	protected MultiplayerScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		ButtonWidget tools = new ButtonWidget(5, 10,  80, 20, new TranslatableText("Tools"), (buttonWidget) -> {
			Helper.minecraftClient.openScreen(new ToolsMain(this));
		});
		
		addButton(tools);
		
		ButtonWidget accButton = new TexturedButtonWidget(this.width / 2 - 180, this.height - 52, 20, 20, 0, 0, 0,
				new Identifier("infinity", "alt.png"), 20, 20,
				buttonWidget -> Helper.minecraftClient.openScreen(new GuiAccountManager(this)),
				new TranslatableText("Account"));

		addButton(accButton);

	}

}
