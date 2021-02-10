package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import me.infinity.event.TickEvent;
import me.infinity.utils.Helper;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(at = @At("TAIL"), method = "tick")
	private void tick(CallbackInfo info) {
		if (Helper.getUpdateUtil().canUpdate()) {
			TickEvent tickEvent = new TickEvent();
			EventManager.call(tickEvent);
		}
	}

}
