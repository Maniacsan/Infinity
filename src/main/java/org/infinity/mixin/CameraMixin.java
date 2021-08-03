package org.infinity.mixin;

import org.infinity.features.module.world.CameraClip;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public class CameraMixin {

	@Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
	private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
		if (InfMain.getModuleManager().get(CameraClip.class).isEnabled()) {
			info.setReturnValue(desiredCameraDistance);
		}
	}

}
