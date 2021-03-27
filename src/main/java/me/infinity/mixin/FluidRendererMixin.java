package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.infinity.InfMain;
import me.infinity.features.module.visual.XRay;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(BlockRenderView extendedBlockView_1, BlockPos blockPos_1, VertexConsumer vertexConsumer_1, FluidState fluidState_1,
			CallbackInfoReturnable<Boolean> callbackInfo) {
		XRay xray = ((XRay) InfMain.getModuleManager().getModuleByClass(XRay.class));

		if (xray.isEnabled() && !xray.isValid(fluidState_1.getBlockState().getBlock())) {
			callbackInfo.setReturnValue(false);
		}
	}

	@Inject(method = "isSideCovered", at = @At("HEAD"), cancellable = true)
	private static void isSideCovered(BlockView blockView_1, BlockPos blockPos_1, Direction direction_1, float float_1, CallbackInfoReturnable<Boolean> callbackInfo) {
		XRay xray = ((XRay) InfMain.getModuleManager().getModuleByClass(XRay.class));

		if (xray.isEnabled() && xray.isValid(blockView_1.getBlockState(blockPos_1).getBlock())) {
			callbackInfo.setReturnValue(false);
		}
	}

}
