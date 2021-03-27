package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.infinity.InfMain;
import me.infinity.features.module.player.FastBreak;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Inject(method = { "getBlockBreakingSpeed" }, at = { @At("RETURN") }, cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
		FastBreak fastBreak = ((FastBreak) InfMain.getModuleManager().getModuleByClass(FastBreak.class));
		if (!fastBreak.isEnabled())
			return;
		cir.setReturnValue(Float.valueOf((float) (((Float) cir.getReturnValue()).floatValue()
				* ((Double) fastBreak.speed.getCurrentValueDouble()))));
	}

}
