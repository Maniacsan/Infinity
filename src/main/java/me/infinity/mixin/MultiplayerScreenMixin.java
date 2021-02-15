package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.infinity.ui.account.GuiAccountSwitcher;
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
		ButtonWidget accButton = new TexturedButtonWidget(this.width / 2 - 180, this.height - 52, 20, 20, 0, 0, 0,
				new Identifier("infinity", "alt.png"), 20, 20,
				buttonWidget -> Helper.minecraftClient.openScreen(new GuiAccountSwitcher(this)),
				new TranslatableText("Account"));

		addButton(accButton);

	}

}
