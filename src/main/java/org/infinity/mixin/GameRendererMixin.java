package org.infinity.mixin;

import org.infinity.features.module.visual.NoHurtCam;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void onHurtCam(CallbackInfo ci) {
		if (InfMain.getModuleManager().getModuleByClass(NoHurtCam.class).isEnabled())
			ci.cancel();
	}
}
