package org.infinity.mixin;

import org.infinity.features.module.visual.XRay;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

	@Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
	public void getAmbientOcclusionLightLevel(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
			CallbackInfoReturnable<Float> callbackInfoReturnable) {
		if (InfMain.getModuleManager().getModuleByClass(XRay.class).isEnabled()) {
			callbackInfoReturnable.setReturnValue(1f);
		}
	}

}
