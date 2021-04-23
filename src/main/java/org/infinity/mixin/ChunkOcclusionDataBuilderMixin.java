package org.infinity.mixin;

import org.infinity.InfMain;
import org.infinity.features.module.visual.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionDataBuilderMixin {

	@Inject(method = "markClosed", at = @At("HEAD"), cancellable = true)
	public void markClosed(BlockPos pos, CallbackInfo callback) {
		if (InfMain.getModuleManager().getModuleByClass(XRay.class).isEnabled()) {
			callback.cancel();
		}
	}

}
