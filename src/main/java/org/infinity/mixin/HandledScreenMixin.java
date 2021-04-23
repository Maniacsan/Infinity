package org.infinity.mixin;

import org.infinity.ui.inventory.InventoryLogo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {

	@Shadow
	private int x;

	@Shadow
	private int y;

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		InventoryLogo logo = new InventoryLogo(width, height, x, y);
		logo.onRender(matrices);
	}

}
