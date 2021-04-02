package me.infinity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkmagician6.eventapi.EventManager;

import me.infinity.event.ClickEvent;
import me.infinity.event.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	public abstract Profiler getProfiler();

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		TickEvent tickEvent = new TickEvent();
		EventManager.call(tickEvent);
	}

	@Inject(at = {
			@At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0) }, method = {
					"doAttack()V" }, cancellable = true)
	private void onClick(CallbackInfo info) {
		ClickEvent clickEvent = new ClickEvent();
		EventManager.call(clickEvent);

		if (clickEvent.isCancelled())
			info.cancel();
	}

}
