package org.infinity.mixin;

import org.infinity.features.module.movement.NoSwim;
import org.infinity.main.InfMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	
	@Inject(method = "isInSwimmingPose", at = @At("HEAD"), cancellable = true)
	public void isInSwimmingPose(CallbackInfoReturnable<Boolean> ci) {
		if (InfMain.getModuleManager().getModuleByClass(NoSwim.class).isEnabled()) 
			ci.setReturnValue(false);
	}

}
