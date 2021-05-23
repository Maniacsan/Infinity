package org.infinity.mixin;

import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	// fabricmc mixin documentation

	// this module bind keydown process
	@Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5), cancellable = true)
	private void onKey(long window, int key, int scancode, int i, int j, CallbackInfo callbackInfo) {
		if (key != -1 || key != -2) {
			InfMain.getHookManager().onKeyPressed(key);
			InfMain.getHookManager().onMacro(key);
		}
	}
}
