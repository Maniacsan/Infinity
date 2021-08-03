package org.infinity.mixin;

import org.infinity.features.module.movement.NoSwim;
import org.infinity.features.module.player.FastBreak;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(method = { "getBlockBreakingSpeed" }, at = { @At("RETURN") }, cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
		FastBreak fastBreak = ((FastBreak) InfMain.getModuleManager().get(FastBreak.class));
		if (fastBreak.isEnabled()) {
			cir.setReturnValue((float) (cir.getReturnValue() * fastBreak.speed.getCurrentValueDouble()));
		}
	}

	@Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
	public void isOnSwimming(CallbackInfoReturnable<Boolean> ci) {
		if (InfMain.getModuleManager().get(NoSwim.class).isEnabled())
			ci.setReturnValue(false);
	}

}
