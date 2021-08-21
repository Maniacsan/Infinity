package org.infinity.mixin;

import org.infinity.features.module.misc.NameProtect;
import org.infinity.main.InfMain;
import org.infinity.utils.Helper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.scoreboard.ScoreboardPlayerScore;

@Mixin(ScoreboardPlayerScore.class)
public class ScoreboardPlayerScoreMixin {
	
	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	private void getPlayerName(CallbackInfoReturnable<String> ci) {
		NameProtect nameProtect = ((NameProtect) InfMain.getModuleManager().get(NameProtect.class));
		if (nameProtect.isEnabled() && Helper.MC.getSession().getUsername().contains(ci.getReturnValue()))
			ci.setReturnValue(nameProtect.name.getText());
	}

}
